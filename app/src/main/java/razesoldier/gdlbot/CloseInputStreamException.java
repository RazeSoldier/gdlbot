/*
  Author: RazeSoldier (razesoldier@outlook.com)
  License: AGPLv3
  Use Mirai https://github.com/mamoe/mirai
 */

package razesoldier.gdlbot;

/**
 * 当无法关闭{@link java.io.InputStream}时抛出此异常
 */
public class CloseInputStreamException extends RuntimeException {
    public CloseInputStreamException(Throwable throwable) {
        super(throwable);
    }
}
