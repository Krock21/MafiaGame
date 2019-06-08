package me.hwproj.mafiagame.util;

public interface MyConsumer<T> {
    void accept(T t) throws Throwable;
}
