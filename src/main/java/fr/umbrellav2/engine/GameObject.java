package fr.umbrellav2.engine;

import fr.umbrellav2.engine.components.Component;
import fr.umbrellav2.engine.utils.Transform;

import java.util.ArrayList;
import java.util.List;

public class GameObject {

    private static int instances;

    private int id;
    private List<Component> components;

    private Transform transform;

    public GameObject() {
        instances++;
        this.id = instances;
        this.transform = new Transform();
        this.components = new ArrayList<>();
    }

    public GameObject(Transform transform) {
        instances++;
        this.id = instances;
        this.transform = transform;
        this.components = new ArrayList<>();
    }

    public void init() {
        for (Component c : components) {
            c.init();
        }
    }

    public void update() {
        for (Component c : components) {
            c.update();
        }
    }

    public <T extends Component> T getComponent(Class<T> componentClass) {
        for (Component c : components) {
            if (componentClass.isAssignableFrom(c.getClass())) {
                return componentClass.cast(c);
            }
        }
        return null;
    }

    public <T extends Component> void removeComponent(Class<T> componentClass) {
        for (int i = 0; i < components.size(); i++) {
            if (componentClass.isAssignableFrom(components.get(i).getClass())) {
                components.remove(components.get(i));
                return;
            }
        }
    }

    public void addComponent(Component c) {
        this.components.add(c);
        c.setParentGameObject(this);
    }

    public Transform getTransform() {
        return transform;
    }
}
