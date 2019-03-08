package com.supermarket.kata.supermarketkata.domain.orders;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.supermarket.kata.supermarketkata.domain.billing.ProductBillingStrategy;
import com.supermarket.kata.supermarketkata.domain.billing.defaultStrategies.ThreeForOneProductBillingStrategy;
import com.supermarket.kata.supermarketkata.domain.products.Product;
import com.supermarket.kata.supermarketkata.domain.products.ProductBillingType;
import org.junit.jupiter.api.*;

@DisplayName("Given an Ordered product")
class OrderItemTest {


    @DisplayName("If a negative amount is specified")
    @Nested
    class OrderItemWithNegativeAmount{

        private Product product;

        @BeforeEach
        void setup() {

            this.product = new Product("product-mock", 1.5f, ProductBillingType.UNIT);
        }
        @Test
        @DisplayName("Then a NegativeAmountException should be thrown")
        void shouldThrowNegativeAmountException(){
            Assertions.assertThrows(NegativeAmountException.class, ()->{

                OrderItem orderItem = new OrderItem(this.product, -5);
            });
        }
    }

    /*
        Let's say for example that a person wants to buy a half can of coke
    * */
    @DisplayName("If the amount specified is not compatible with the product billing type")
    @Nested
    class OrderItemWithIncompatibleUnit{


        private Product drink;

        @BeforeEach
        void setup() {

            drink = new Product("drink", 1.5f, ProductBillingType.UNIT);
        }
        @Test
        @DisplayName("Then an IncompatibleUnitException should be thrown")
        void shouldThrowIncompatibleUnitException(){
            Assertions.assertThrows(IncompatibleUnitException.class, ()->{

                OrderItem orderItem = new OrderItem(drink, 1.4f);
            });
        }
    }
    @DisplayName("If the ordered product has not pricing strategy")
    @Nested
    class OrderedProductWithNoPricingStrategy{

        private Product cocaCola;

        @BeforeEach
        void setup() {

            cocaCola = new Product("Coca-Cola", 1.5f, ProductBillingType.UNIT);
        }
        @Test
        @DisplayName("Then the default BasicPricingStrategy should be used")
        void shouldReturn3WhenUnitPriceIs1_5AndAmountIs2(){

            try {
                OrderItem orderItem = new OrderItem(cocaCola, 2);
                Assertions.assertEquals(orderItem.price(), 3);

            } catch (Exception e) {

                Assertions.fail("Error during item creation");
            }
        }
    }

    @DisplayName("If the ordered product has multiple pricing strategies")
    @Nested
    class OrderedProductWithMultiplePricingStrategies{

        private Product coffee;

        @BeforeEach
        void setup() {

            coffee = new Product("Coffee", 0.5f, ProductBillingType.WEIGHT);

            coffee.addBillingStrategy(new ThreeForOneProductBillingStrategy());

            /*
             * Custom runtime pricing strategy
             * If we buy 2 units the third is free
             * */
            coffee.addBillingStrategy((product, amount)->{

                int numberOfThrees = amount.intValue()/3;

                //Every three items cost two items because the third is free
                return product.getUnitPrice() * (amount - numberOfThrees);
            });
        }
        @Test
        @DisplayName("Then the minimal cost pricing strategy should be executed")
        void shouldReturn2_5WhenUnitPriceIs0_5AndAmountIs11(){

            try {
                OrderItem orderItem = new OrderItem(coffee, 11);
                Assertions.assertEquals(orderItem.price(), 3);

            } catch (Exception e) {

                Assertions.fail("Error during item creation");
            }
        }
    }

}