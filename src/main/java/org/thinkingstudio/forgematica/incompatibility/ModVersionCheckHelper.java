package org.thinkingstudio.forgematica.incompatibility;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.LoadingModList;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.forgespi.language.IModInfo;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.VersionRange;
import org.thinkingstudio.forgematica.Forgematica;

import java.util.Optional;

// https://github.com/Fallen-Breath/conditional-mixin/blob/master/forge/src/main/java/me/fallenbreath/conditionalmixin/api/util/forge/VersionCheckerImpl.java
public class ModVersionCheckHelper {
    /**
     * {@link net.minecraftforge.fml.loading.LoadingModList} does not exist until forge
     */
    private static Optional<IModFileInfo> getLoadingModFileInfo(String modId)
    {
        try
        {
            return Optional.ofNullable(LoadingModList.get()).map(ml -> ml.getModFileById(modId));
        }
        catch (Exception e)
        {
            return Optional.empty();
        }
    }

    private static Optional<IModFileInfo> getModFileInfo(String modId)
    {
        Optional<IModFileInfo> mod = getLoadingModFileInfo(modId);
        if (!mod.isPresent())
        {
            mod = Optional.ofNullable(ModList.get()).map(ml -> ml.getModFileById(modId));
        }
        return mod;
    }

    private static Optional<ArtifactVersion> getModVersion(IModFileInfo modFileInfo)
    {
        // IModFileInfo#versionString might not exist in old forge loader
        for (IModInfo mod : modFileInfo.getMods())
        {
            return Optional.of(mod.getVersion());
        }
        return Optional.empty();
    }

    public static boolean doesModVersionSatisfyPredicate(String modId, String versionPredicate)
    {
        Optional<ArtifactVersion> versionOpt = getModFileInfo(modId)
                .filter(modFileInfo -> !modFileInfo.getMods().isEmpty())
                .flatMap(ModVersionCheckHelper::getModVersion);
        if (!versionOpt.isPresent()) return false;
        ArtifactVersion version = versionOpt.get();

        try
        {
            // TODO: consistent version predicate parsing across loaders
            return VersionRange.createFromVersionSpec(versionPredicate).containsVersion(version);
        }
        catch (Exception e)
        {
            Forgematica.LOGGER.error("Failed to parse version or version predicate {} {}: {}", version, versionPredicate, e);
        }
        return false;
    }
}
