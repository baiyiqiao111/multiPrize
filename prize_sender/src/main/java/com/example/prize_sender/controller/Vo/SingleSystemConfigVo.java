package com.example.prize_sender.controller.Vo;

public class SingleSystemConfigVo {
    private BaseVo baseVo;
    private SystemConfigVo systemConfigVo;

    public BaseVo getBaseVo() {
        return baseVo;
    }

    public void setBaseVo(BaseVo baseVo) {
        this.baseVo = baseVo;
    }

    public SystemConfigVo getSystemConfigVo() {
        return systemConfigVo;
    }

    public void setSystemConfigVo(SystemConfigVo systemConfigVo) {
        this.systemConfigVo = systemConfigVo;
    }

    public SingleSystemConfigVo(BaseVo baseVo, SystemConfigVo systemConfigVo) {
        this.baseVo = baseVo;
        this.systemConfigVo = systemConfigVo;
    }

    public SingleSystemConfigVo() {
    }
}
