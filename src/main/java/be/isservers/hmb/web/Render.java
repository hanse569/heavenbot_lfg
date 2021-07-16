package be.isservers.hmb.web;

import java.io.IOException;

interface Render {
    String build() throws IOException;
}
