# Refactoring Assignment — Notification Processing System (OOP + SOLID)

## 📘 Overview
You are given a working but poorly designed notification-processing system.  
Your task is to **refactor** the code to apply proper **OOP design** and **SOLID principles**, while keeping all external behavior the same.

The starter code mixes validation and delivery logic inside a single procedural method.  
You must redesign it into a clean, extensible, object-oriented architecture.

---

# 🎯 Your Goal

Refactor the `NotificationManager` so that it becomes clean, modular, and fully SOLID-compliant.

---

## Requirements:
- All existing functionality preserved
- New notification types can be added without modifying core
- New delivery channels can be added easily
- Adheres to all SOLID Principles

## Deliverables
 - `Fully Refactored Code`
 - `Short Document` explaining the Violations you noticed and how you improved it.(you can substitute this with comprehensive comments in your code.)

## Helper questions
These are some questions to ask yourself as you refactor.

### 1."What are the distinct reasons this class might change?"
- If validation rules change, what gets modified?
- If delivery mechanisms change, what gets modified?
- If notification types change, what gets modified?

### 2."Can I describe each class's responsibility in one sentence without using 'and'?"

### 3."Are there any hidden responsibilities mixed in?"
- Is data transformation mixed with business logic?
- Is error handling mixed with core processing?
- Is configuration mixed with execution?

### 4."What happens when we need to add a new notification type?"
- How many files need to be modified?
- Can we add SMS notifications without touching existing code?
- Are we using abstractions that allow extension?
- Are concrete implementations hardcoded anywhere?
- Do we have switch statements or if-else chains that need modification?
- Can we plugin new behaviors without changing core logic?

### 5."Can I substitute any Notification implementation without breaking the system?"
- Do all notifications support the same contract?
- Are there any type checks or instanceof operations?
- Do subtypes strengthen preconditions or weaken postconditions?

### 6."What assumptions are we making about specific notification types?"
- Are we assuming all notifications have priorities?
- Are we assuming all notifications can be rendered for email?
- Do we have any "surprise" behaviors in subtypes?

### 7."If I create a new Notification type, what exceptions might I encounter?"
- Will it break existing validators?
- Will it work with all channels?
- Are there any hidden dependencies on specific types?

### 8."What methods would a simple notification be forced to implement?"

### 9."Which clients use which methods of our interfaces?"
- Do validators use the same methods as channels?
- Can we split interfaces based on client needs?
- Are there "fat" interfaces that should be decomposed?

### 10."What happens when a notification doesn't need certain capabilities?"
- Do we throw UnsupportedOperationException?
- Do we return null or default values?
- Can we avoid implementing unused methods entirely?

### 11."What are the high-level and low-level modules in this system?"
- Is the processor depending on concrete validators?
- Is the processor depending on concrete channels?
- Are we depending on abstractions or implementations?

### 12."Where are we creating dependencies, and can we invert them?"
- Who instantiates the validators and channels?
- Can we use dependency injection?
- Are we violating the "don't call us, we'll call you" principle?

### 14. "How does our design handle change?"
- If business rules change frequently, is our design resilient?
- If we need to add cross-cutting concerns (logging, metrics), where would they go?
- How easy is it to unit test each component in isolation?

