package be.isservers.hmb.command;

public interface IPublicCommand extends ICommand{
    void handle(PublicCommandContext ctx);
}
