package org.rublin.provider;

import java.util.List;

public abstract class AbstractMarketplace implements Marketplace{
    protected List<String> pairs;
    protected static final String SPLIT = ",";

    @Override
    public List<String> getAvailablePairs() {
        return pairs;
    }
}
