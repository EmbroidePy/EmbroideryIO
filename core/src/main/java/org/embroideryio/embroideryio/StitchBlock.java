package org.embroideryio.embroideryio;

public interface StitchBlock {
    EmbThread getThread();
    Points getPoints();
    int getType();
}
