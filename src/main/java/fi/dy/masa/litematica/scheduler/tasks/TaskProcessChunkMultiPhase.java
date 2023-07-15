package fi.dy.masa.litematica.scheduler.tasks;

import fi.dy.masa.litematica.config.Configs;
import fi.dy.masa.litematica.data.DataManager;
import fi.dy.masa.litematica.util.ToBooleanFunction;
import fi.dy.masa.malilib.util.IntBoundingBox;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public abstract class TaskProcessChunkMultiPhase extends TaskProcessChunkBase
{
    protected TaskPhase phase = TaskPhase.INIT;
    @Nullable protected ChunkPos currentChunkPos;
    @Nullable protected IntBoundingBox currentBox;
    @Nullable protected Iterator<Entity> entityIterator;
    @Nullable protected Iterator<BlockPos> positionIterator;
    protected int maxCommandsPerTick = 16;
    protected int processedChunksThisTick;
    protected int sentCommandsThisTick;
    protected long gameRuleProbeTimeout;
    protected long maxGameRuleProbeTime = 2000000000L; // 2 second timeout
    protected long taskStartTimeForCurrentTick;
    protected boolean shouldEnableFeedback;

    protected ToBooleanFunction<Text> gameRuleListener = this::checkCommandFeedbackGameRuleState;
    protected Runnable initTask = this::initPhaseStartProbe;
    protected Runnable probeTask = this::probePhase;
    protected Runnable waitForChunkTask = this::fetchNextChunk;
    protected Runnable processBoxBlocksTask;
    protected Runnable processBoxEntitiesTask;

    public enum TaskPhase
    {
        INIT,
        GAME_RULE_PROBE,
        WAIT_FOR_CHUNKS,
        PROCESS_BOX_BLOCKS,
        PROCESS_BOX_ENTITIES,
        FINISHED
    }

    protected TaskProcessChunkMultiPhase(String nameOnHud)
    {
        super(nameOnHud);
    }

    protected boolean executeMultiPhase()
    {
        this.taskStartTimeForCurrentTick = Util.getMeasuringTimeNano();
        this.sentCommandsThisTick = 0;
        this.processedChunksThisTick = 0;

        if (this.phase == TaskPhase.INIT)
        {
            this.initTask.run();
        }

        if (this.phase == TaskPhase.GAME_RULE_PROBE)
        {
            this.probeTask.run();
            return false;
        }

        if (this.currentChunkPos != null && this.canProcessChunk(this.currentChunkPos) == false)
        {
            return false;
        }

        int commandsLast = -1;
        ChunkPos lastChunk = this.currentChunkPos;

        while (this.sentCommandsThisTick < this.maxCommandsPerTick &&
               (this.sentCommandsThisTick > commandsLast || Objects.equals(lastChunk, this.currentChunkPos) == false))
        {
            long currentTime = Util.getMeasuringTimeNano();
            long elapsedTickTime = (currentTime - this.taskStartTimeForCurrentTick);

            if (elapsedTickTime >= 25000000L)
            {
                break;
            }

            commandsLast = this.sentCommandsThisTick;
            lastChunk = this.currentChunkPos;

            if (this.phase == TaskPhase.WAIT_FOR_CHUNKS)
            {
                this.waitForChunkTask.run();
            }

            if (this.phase == TaskPhase.PROCESS_BOX_BLOCKS)
            {
                this.processBoxBlocksTask.run();
            }

            if (this.phase == TaskPhase.PROCESS_BOX_ENTITIES)
            {
                this.processBoxEntitiesTask.run();
            }

            if (this.phase == TaskPhase.FINISHED)
            {
                return true;
            }
        }

        if (this.processedChunksThisTick > 0)
        {
            this.updateInfoHudLines();
        }

        return false;
    }

    protected void initPhaseStartProbe()
    {
        if (Configs.Generic.COMMAND_DISABLE_FEEDBACK.getBooleanValue())
        {
            DataManager.addChatListener(this.gameRuleListener);
            this.mc.player.sendChatMessage("/gamerule sendCommandFeedback");
            this.gameRuleProbeTimeout = Util.getMeasuringTimeNano() + this.maxGameRuleProbeTime;
            this.phase = TaskPhase.GAME_RULE_PROBE;
        }
        else
        {
            this.shouldEnableFeedback = false;
            this.phase = TaskPhase.WAIT_FOR_CHUNKS;
        }
    }

    protected void probePhase()
    {
        if (Util.getMeasuringTimeNano() > this.gameRuleProbeTimeout)
        {
            this.shouldEnableFeedback = false;
            this.phase = TaskPhase.WAIT_FOR_CHUNKS;
        }
    }

    protected boolean checkCommandFeedbackGameRuleState(Text message)
    {
        if (message instanceof TranslatableText translatableText)
        {
            if ("commands.gamerule.query".equals(translatableText.getKey()))
            {
                this.shouldEnableFeedback = translatableText.getString().contains("true");
                this.phase = TaskPhase.WAIT_FOR_CHUNKS;

                if (this.shouldEnableFeedback)
                {
                    this.mc.player.sendChatMessage("/gamerule sendCommandFeedback false");
                }

                return true;
            }
        }

        return false;
    }

    protected void fetchNextChunk()
    {
        if (this.pendingChunks.isEmpty() == false)
        {
            this.sortChunkList();

            ChunkPos pos = this.pendingChunks.get(0);

            if (this.canProcessChunk(pos))
            {
                this.currentChunkPos = pos;
                this.onNextChunkFetched(pos);
            }
        }
        else
        {
            this.phase = TaskPhase.FINISHED;
            this.finished = true;
        }
    }

    protected void onNextChunkFetched(ChunkPos pos)
    {
    }

    protected void startNextBox(ChunkPos pos)
    {
        List<IntBoundingBox> list = this.boxesInChunks.get(pos);

        if (list.isEmpty() == false)
        {
            this.currentBox = list.get(0);
            this.onStartNextBox(this.currentBox);
        }
        else
        {
            this.currentBox = null;
            this.phase = TaskPhase.WAIT_FOR_CHUNKS;
        }
    }

    protected void onStartNextBox(IntBoundingBox box)
    {
    }

    protected void onFinishedProcessingBox(ChunkPos pos, IntBoundingBox box)
    {
        this.boxesInChunks.remove(pos, box);
        this.currentBox = null;
        this.entityIterator = null;
        this.positionIterator = null;

        if (this.boxesInChunks.get(pos).isEmpty())
        {
            this.finishProcessingChunk(pos);
        }
        else
        {
            this.startNextBox(pos);
        }
    }

    protected void finishProcessingChunk(ChunkPos pos)
    {
        this.boxesInChunks.removeAll(pos);
        this.pendingChunks.remove(pos);
        this.currentChunkPos = null;
        ++this.processedChunksThisTick;
        this.phase = TaskPhase.WAIT_FOR_CHUNKS;
        this.onFinishedProcessingChunk(pos);
    }

    protected void onFinishedProcessingChunk(ChunkPos pos)
    {
    }

    protected void sendCommand(String command, ClientPlayerEntity player)
    {
        this.sendCommandToServer(command, player);
        ++this.sentCommandsThisTick;
    }

    protected void sendCommandToServer(String command, ClientPlayerEntity player)
    {
        if (command.length() > 0 && command.charAt(0) != '/')
        {
            player.sendChatMessage("/" + command);
        }
        else
        {
            player.sendChatMessage(command);
        }
    }
}
