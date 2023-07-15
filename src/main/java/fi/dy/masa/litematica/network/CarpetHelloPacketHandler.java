package fi.dy.masa.litematica.network;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.litematica.data.DataManager;
import fi.dy.masa.malilib.network.IPluginChannelHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.List;

public class CarpetHelloPacketHandler implements IPluginChannelHandler
{
    public static final CarpetHelloPacketHandler INSTANCE = new CarpetHelloPacketHandler();

    private final List<Identifier> channels = ImmutableList.of(new Identifier("carpet:hello"));

    @Override
    public boolean registerToServer()
    {
        return false;
    }

    @Override
    public boolean usePacketSplitter()
    {
        return false;
    }

    @Override
    public List<Identifier> getChannels()
    {
        return this.channels;
    }

    @Override
    public void onPacketReceived(PacketByteBuf buf)
    {
        DataManager.setIsCarpetServer(true);
    }
}
