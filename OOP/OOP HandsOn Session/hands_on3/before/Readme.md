# Hands On Session Task
## Simple ECommerce App Refactoring 
- **From** main method (with procedural approach) 
- **To** layered approach (Repo, Service, Controller, View) with OOP, Desing Principes & Patterns

## Some Hints
1. Try to determine the Required data models (like Product, ..)
2. In Repo layer, make it in memory (like list with some data examples)
3. For Repo, Think about one repository (you can identify it from before code (what is the object we need to apply CRUD Operations on it?))
4. For Service, Think about one service what is the actual business logic in our code?? (Think in checkout flow and order processing)
5. For View, for simplicity, you can make all function in one view class
6. The controller class, It will manage the flow between View and Service (you need to create the instance of Service (and it's dependencies) and View class)
7. Start think in applying Design Principles & Patterns
8. In Patterns (try to think in Strategy, Factory, Observer)

## Deadline
- Mon 2026-02-16 11:59 PM