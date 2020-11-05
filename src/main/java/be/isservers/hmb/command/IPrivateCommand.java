package be.isservers.hmb.command;

public interface IPrivateCommand extends ICommand{
    void handle(PrivateCommandContext ctx);
}
