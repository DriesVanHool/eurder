package com.switchfully.eurder.domain.security;

import java.util.List;

public enum Role {
    CUSTOMER(Feature.PLACE_ORDER, Feature.GET_ORDER_REPORT), ADMIN(Feature.GET_CUSTOMERS, Feature.ADD_ITEM, Feature.GET_ITEMS, Feature.PLACE_ORDER, Feature.UPDATE_ITEM, Feature.GET_ORDER_REPORT);

    private final List<Feature> features;

    Role(Feature... features) {
        this.features = List.of(features);
    }

    public boolean hasFeature(Feature feature) {
        return this.features.contains(feature);
    }
}
