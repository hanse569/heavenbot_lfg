package be.isservers.hmb.command;

import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

import java.util.List;

public class PrivateCommandContext {
    private final PrivateMessageReceivedEvent event;
    private final List<String> args;

    public PrivateCommandContext(PrivateMessageReceivedEvent event, List<String> args) {
        this.event = event;
        this.args = args;
    }

    public PrivateMessageReceivedEvent getEvent() {
        return this.event;
    }

    public List<String> getArgs() {
        return this.args;
    }
}
