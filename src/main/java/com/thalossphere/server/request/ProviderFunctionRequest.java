package com.thalossphere.server.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class ProviderFunctionRequest implements Serializable {

    private String providerName;

    private String url;

}
