package be.isservers.hmb.slashCommand;

import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.util.ArrayList;
import java.util.List;

public class SlashCommandParameter {
    private final ArrayList<SlashCommandParamaterItem> list = new ArrayList<>();

    public SlashCommandParameter add(OptionType optionType, String name, String description) {
        list.add(new SlashCommandParamaterItem(optionType,name,description));
        return this;
    }

    public SlashCommandParameter add(OptionType optionType, String name, String description, Boolean isRequired) {
        list.add(new SlashCommandParamaterItem(optionType,name,description,isRequired));
        return this;
    }

    public List<SlashCommandParamaterItem> build() {
        return list;
    }


}
