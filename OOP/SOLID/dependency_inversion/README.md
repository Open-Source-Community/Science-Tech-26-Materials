# Dependency Inversion Principle

**High-level modules should not depend on low-level modules. Both should depend on abstractions.**

**Abstractions should not depend on details. Details should depend on abstractions.**

The DIP is concerned with reusability. The high-level modules or interfaces of an application should only be describing the "general flow" of behavior. In some cases this may be considered "business logic". Meanwhile, the low-level modules are written in such a way to apply their concrete details to the abstraction. 

