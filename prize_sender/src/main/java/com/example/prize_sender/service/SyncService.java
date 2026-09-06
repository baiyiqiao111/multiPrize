package com.example.prize_sender.service;

import com.example.prize_sender.controller.cmd.SyncCmd;

public interface SyncService {
    void sync(SyncCmd syncCmd);
}
