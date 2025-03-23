package com.thalossphere.server.notify;

import com.thalossphere.server.entity.Cmd;

import java.util.List;

public interface NotificationListener {

    void handler(List<Cmd> cmdList);


}
