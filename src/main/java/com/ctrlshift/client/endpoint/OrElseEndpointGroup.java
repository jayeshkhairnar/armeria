package com.ctrlshift.client.endpoint;

import static java.util.Objects.requireNonNull;

import java.util.List;

import com.ctrlshift.client.Endpoint;
import com.ctrlshift.commons.util.AbstractListenable;

final class OrElseEndpointGroup extends AbstractListenable<List<Endpoint>> implements EndpointGroup {
    private final EndpointGroup first;
    private final EndpointGroup second;

    OrElseEndpointGroup(EndpointGroup first, EndpointGroup second) {
        this.first = requireNonNull(first, "first");
        this.second = requireNonNull(second, "second");
        first.addListener(unused -> notifyListeners(endpoints()));
        second.addListener(unused -> notifyListeners(endpoints()));
    }

    @Override
    public List<Endpoint> endpoints() {
        List<Endpoint> endpoints = first.endpoints();
        if (!endpoints.isEmpty()) {
            return endpoints;
        }
        return second.endpoints();
    }
}
