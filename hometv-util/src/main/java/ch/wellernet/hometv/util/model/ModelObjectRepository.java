/**
 *
 */
package ch.wellernet.hometv.util.model;

/**
 * @author Lucien Weller <lucien@wellernet.ch>
 */
public interface ModelObjectRepository<ID, T extends IdentifyableObject<ID>> {
    public void attach(T object);
}
