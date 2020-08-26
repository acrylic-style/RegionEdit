package xyz.acrylicstyle.region.api.util;

import util.reflect.Ref;
import util.reflect.RefClass;
import xyz.acrylicstyle.tomeito_api.utils.ReflectionUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static xyz.acrylicstyle.shared.NMSAPI.getClassWithoutException;

public class NMSClasses {
    public static final Class<?> BlockPosition = getClassWithoutException("BlockPosition");
    public static final Class<?> IBlockData = getClassWithoutException("IBlockData");
    public static final Class<?> World = getClassWithoutException("World");
    public static final Class<?> EntityPlayer = getClassWithoutException("EntityPlayer");
    public static final Class<?> PlayerConnection = getClassWithoutException("PlayerConnection");
    public static final Class<?> Packet = getClassWithoutException("Packet");
    public static final Class<?> Chunk = getClassWithoutException("Chunk");
    public static final Class<?> Block = getClassWithoutException("Block");

    public static final Field playerConnection = Ref.getClass(EntityPlayer).getField("playerConnection").getField();

    public static final Method sendPacket = Ref.getClass(PlayerConnection).getMethod("sendPacket", Packet).getMethod();

    private static boolean blocksChecked = false;
    private static RefClass<Object> Blocks_cached;
    public static RefClass<Object> Blocks() {
        if (blocksChecked) return Blocks_cached;
        try {
            ReflectionUtil.getNMSClass("Blocks");
            Blocks_cached = Ref.forName(ReflectionUtil.getNMSPackage() + ".Blocks");
        } catch (ClassNotFoundException ignore) {}
        blocksChecked = true;
        return Blocks_cached;
    }
}
