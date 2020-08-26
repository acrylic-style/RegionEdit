package xyz.acrylicstyle.region.api.schematic;

import net.querz.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;
import util.ICollectionList;
import xyz.acrylicstyle.region.api.block.state.BlockState;

import java.io.File;

public interface Schematic {
    @NotNull
    CompoundTag getTag();

    @NotNull ICollectionList<BlockState> getBlocks();

    void save(@NotNull File file);
}
