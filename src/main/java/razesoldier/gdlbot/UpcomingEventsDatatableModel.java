/*
  Author: RazeSoldier (razesoldier@outlook.com)
  License: AGPLv3
  Use Mirai https://github.com/mamoe/mirai
 */

package razesoldier.gdlbot;

import java.util.List;

public record UpcomingEventsDatatableModel(List<Data> data) {
    public record Data(String name, String op_type, String start_time, String fleet_priority) {
    }
}
