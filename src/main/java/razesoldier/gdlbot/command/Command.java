/*
  Author: RazeSoldier (razesoldier@outlook.com)
  License: AGPLv3
  Use Mirai https://github.com/mamoe/mirai
 */

package razesoldier.gdlbot.command;

import net.mamoe.mirai.contact.Contact;

public interface Command {
    void execute();
    void setRecipient(Contact contact);
}
