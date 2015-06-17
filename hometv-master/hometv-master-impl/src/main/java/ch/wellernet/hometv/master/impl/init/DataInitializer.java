/**
 *
 */
package ch.wellernet.hometv.master.impl.init;

/**
 * If a component implementing this interface is present in classpath, it will be called after HomeTV Master has successfully been initialized. It's
 * the opportunity to create some initial data.
 *
 * @author Lucien Weller <lucien@wellernet.ch>
 */
public interface DataInitializer {
    /**
     * Does the initialization.
     */
    public void initData();
}
