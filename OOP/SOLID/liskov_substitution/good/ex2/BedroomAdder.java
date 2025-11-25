/**
 * Created by mrk on 4/8/14.
 * The key to understanding the Liskov Substitution Principle is thinking about _processes that use_ 
 * (sub)classes, rather than the (sub)classes themselves. In the bad example here, the UnitUpgrader
 *  purports to accept any Apartment (an abstract class) and upgrade it. However, once the UnitUpgrader
 *  starts upgrading the apartment (`upgrade(Apartment)`), it checks the specific class/subtype of the 
 * Apartment object to make sure it doesn't add a bedroom to a Studio (which by definition has zero
 *  bedrooms). A Studio object therefore cannot be substituted in for any Apartment.

If you don't follow the LSP, external processes will either break, behave improperly, or need to know too much information.

 */
public class BedroomAdder {
    public void addBedroom(PenthouseSuite penthouse) {
        penthouse.numberOfBedrooms += 1;
    }
}
