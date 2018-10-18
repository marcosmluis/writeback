import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.mapping.*;
import org.hibernate.mapping.Column;

import java.util.*;

import org.hibernate.MappingException;

public class CustomizableEntityManagerImpl implements CustomizableEntityManager {
    private Component customProperties;
    private Class entityClass;
    private StandardServiceRegistry registry;

    public CustomizableEntityManagerImpl(Class entityClass) {
        this.entityClass = entityClass;
    }

    public Class getEntityClass() {
        return entityClass;
    }

    public Component getCustomProperties() {
        if (customProperties == null) {
            Property property = null;

            try {
                property = getPersistentClass().getProperty(CUSTOM_COMPONENT_NAME);
            } catch ( MappingException e) {
                e.printStackTrace();
            }


            customProperties = (Component) property.getValue();
        }
        return customProperties;
    }

    public void addCustomField(String name) {
        SimpleValue simpleValue = new SimpleValue(null);
        simpleValue.addColumn(new Column("fld_" + name));
        simpleValue.setTypeName(String.class.getName());

        PersistentClass persistentClass = getPersistentClass();
        simpleValue.setTable(persistentClass.getTable());

        Property property = new Property();
        property.setName(name);
        property.setValue(simpleValue);

        getCustomProperties().addProperty(property);

        updateMapping();
    }

    public void removeCustomField(String name) {
        Iterator propertyIterator = customProperties.getPropertyIterator();

        while (propertyIterator.hasNext()) {
            Property property = (Property) propertyIterator.next();
            if (property.getName().equals(name)) {
                propertyIterator.remove();
                updateMapping();
                return;
            }
        }
    }

    private synchronized void updateMapping() {

        MappingManager.updateClassMapping(this);
        HibernateUtil.getInstance().reset();
        // updateDBSchema();
    }

    private PersistentClass getPersistentClass() {

        return HibernateUtil.getInstance().getClassMapping(this.entityClass);

    }
}