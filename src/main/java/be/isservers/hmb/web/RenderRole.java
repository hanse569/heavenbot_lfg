package be.isservers.hmb.web;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class RenderRole implements Render{
    String name;
    String image;

    public RenderRole(String name) {
        this.name = name;
        this.image = "https://discord.com/assets/322c936a8c8be1b803cd94861bdfa868.png";
    }

    public RenderRole(String name, String image) {
        this.name = name;
        if (image == null)
            this.image = "https://discord.com/assets/322c936a8c8be1b803cd94861bdfa868.png";
        else
            this.image = image;
    }

    @Override
    public String build() throws IOException {
        String template = new String(Files.readAllBytes(new File(getClass().getClassLoader().getResource("pages/item/detailArchiveItem.peb").getFile()).toPath()));
        template = template.replace("{{name}}", this.name);
        template = template.replace("{{image}}", this.image);
        return template;
    }
}
