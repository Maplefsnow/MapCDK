package me.maplef.mapcdk.exceptions;

public class InsufficientInventorySpaceException extends Exception{
    public InsufficientInventorySpaceException() {
        super();
    }

    public InsufficientInventorySpaceException(int value) {
        super(String.format("背包空间不足，还需要 %d 格子的空间", value));
    }
}
