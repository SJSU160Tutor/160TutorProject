package org.drupal.project.async_command;

/**
 * Simple command to test Drupal database connection.
 */
public class PingMe extends AsyncCommand {

    private String pingMessage;
    private String pongMessage;

    public PingMe(CommandRecord record, GenericDrupalApp drupalApp) {
        super(record, drupalApp);
        if (record.getString1() != null) {
            pingMessage = record.getString1();
        }
    }

    @Override
    public void run() {
        record.setStart(DrupalUtils.getLocalUnixTimestamp());
        if (pingMessage != null) {
            pongMessage = "Pong with message: " + pingMessage;
        } else {
            pongMessage = "Pong.";
        }

        // finish up.
        record.setStatus(Status.SUCCESS);
        record.setMessage(pongMessage);
        record.setEnd(DrupalUtils.getLocalUnixTimestamp());
    }
}
