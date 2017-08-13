package com.example.prodigy.litevervelife.HelpClasses;

/**
 * Created by Prodigy on 15.07.2017.
 */

public interface IService {

    void getGatt(String characteristic);
    void setGatt(String characteristic, byte[] settings);
}
