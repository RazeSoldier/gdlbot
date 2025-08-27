/*
  Author: RazeSoldier (razesoldier@outlook.com)
  License: AGPLv3
  Use Mirai https://github.com/mamoe/mirai
 */

package razesoldier.gdlbot.command;

import net.mamoe.mirai.contact.Contact;

class EmptyCommand implements Command {
    @Override
    public void execute() {
        // Empty command, do nothing
    }

    @Override
    public void setRecipient(Contact contact) {
        // Empty command, do nothing
    }
}
