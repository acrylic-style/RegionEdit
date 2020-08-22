package xyz.acrylicstyle.region.api.schematic;

import net.querz.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;
import util.CollectionList;
import xyz.acrylicstyle.region.api.block.state.BlockState;

import java.io.File;

public interface Schematic {
    @NotNull
    CompoundTag getTag();

    @NotNull
    CollectionList<BlockState> getBlocks();

    void save(@NotNull File file);
}
