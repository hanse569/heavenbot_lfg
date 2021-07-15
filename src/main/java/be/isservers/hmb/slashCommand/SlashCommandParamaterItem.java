package be.isservers.hmb.slashCommand;

import net.dv8tion.jda.api.interactions.commands.OptionType;

public class SlashCommandParamaterItem {
    private OptionType optionType;
    private String name;
    private String description;
    private Boolean isRequired;

    SlashCommandParamaterItem(OptionType optionType,String name,String description) {
        this.optionType = optionType;
        this.name = name;
        this.description = description;
        this.isRequired = false;
    }

    SlashCommandParamaterItem(OptionType optionType,String name,String description,Boolean isRequired) {
        this.optionType = optionType;
        this.name = name;
        this.description = description;
        this.isRequired = isRequired;
    }

    public OptionType getOptionType() {
        return optionType;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Boolean getRequired() {
        return isRequired;
    }
}
