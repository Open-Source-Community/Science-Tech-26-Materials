//Consider a scenario where you have a weather monitoring system.
// Different parts of your application need to be updated
// when the weather conditions change.


///UML photo in session Slides for more info
//solution
import java.util.ArrayList;
import java.util.List;

        

// Observer Interface

interface Observer {

    void update(String weather);

}

        

// Subject Interface

interface Subject {

    void addObserver(Observer observer);

    void removeObserver(Observer observer);

    void notifyObservers();

}

        

// ConcreteSubject Class

class WeatherStation implements Subject {

    private List<Observer> observers = new ArrayList<>();

    private String weather;

            

    @Override

    public void addObserver(Observer observer) {

        observers.add(observer);

    }

            

    @Override

    public void removeObserver(Observer observer) {

        observers.remove(observer);

    }

            

    @Override

    public void notifyObservers() {

        for (Observer observer : observers) {

            observer.update(weather);

        }

    }

            

    public void setWeather(String newWeather) {

        this.weather = newWeather;

        notifyObservers();

    }

}

        

// ConcreteObserver Class

class PhoneDisplay implements Observer {

    private String weather;

            

    @Override

    public void update(String weather) {

        this.weather = weather;

        display();

    }

            

    private void display() {

        System.out.println("Phone Display: Weather updated - " + weather);

    }

}

        

// ConcreteObserver Class

class TVDisplay implements Observer {

    private String weather;

            

    @Override

    public void update(String weather) {

        this.weather = weather;

        display();

    }

            

    private void display() {

        System.out.println("TV Display: Weather updated - " + weather);

    }

}

        

// Usage Class

 class WeatherApp {

    public static void main(String[] args) {

        WeatherStation weatherStation = new WeatherStation();



        Observer phoneDisplay = new PhoneDisplay();

        Observer tvDisplay = new TVDisplay();



        // Register observers

        weatherStation.addObserver(phoneDisplay);

        weatherStation.addObserver(tvDisplay);



        // Simulating weather changes

        weatherStation.setWeather("Sunny");

        weatherStation.setWeather("Rainy");

        weatherStation.setWeather("Cloudy");



        // Remove one observer

        weatherStation.removeObserver(tvDisplay);



        // Notify remaining observer

        weatherStation.setWeather("Windy");

    }

}