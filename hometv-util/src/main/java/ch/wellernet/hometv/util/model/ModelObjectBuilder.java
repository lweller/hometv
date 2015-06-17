/**
 *
 */
package ch.wellernet.hometv.util.model;

/**
 * Abstract base implementation for a builder that allows to build consistent instances of model objects. Especially it may ensure that no new
 * instances exists that are incomplete or not attached to an object repository.
 *
 * @author Lucien Weller <lucien@wellernet.ch>
 */
public abstract class ModelObjectBuilder<ID, T extends IdentifyableObject<ID>> {
    private boolean built;

    public ModelObjectBuilder() {
        built = false;
    }

    /**
     * Builds a concrete instance of model object and attaches it ot the object repository.
     *
     * @param repository
     *            objectrepository to attach the new instance
     * @return the new instance of model object
     * @throws IllegalStateException
     *             if model object has already been built by a previous call to {@link #build(ModelObjectRepository)}
     */
    public T build(ModelObjectRepository<ID, T> repository) throws IllegalStateException {
        if (built) {
            throw new IllegalStateException("instance has already been built by a previous call of this method");
        }
        T object = build();
        built = true;
        repository.attach(object);
        return object;
    }

    /**
     * Concrete implementations should create and return a new instance of builded model object
     *
     * @return the new instance of model object
     */
    protected abstract T build();
}
