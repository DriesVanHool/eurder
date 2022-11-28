package com.switchfully.eurder.domain.security;

import java.util.List;

public enum Role {
    CUSTOMER(), ADMIN();

    private final List<Feature> features;

    Role(Feature... features) {
        this.features = List.of(features);
    }

    public  boolean hasFeature(Feature feature){
        return this.features.contains(feature);
    }
}
