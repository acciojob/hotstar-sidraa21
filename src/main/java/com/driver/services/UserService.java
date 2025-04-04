package com.driver.services;


import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.model.WebSeries;
import com.driver.repository.UserRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    WebSeriesRepository webSeriesRepository;


    public Integer addUser(User user){
        User savedUser = userRepository.save(user);
        return savedUser.getId();
        //Jut simply add the user to the Db and return the userId returned by the repository
       // return null;
    }

    public Integer getAvailableCountOfWebSeriesViewable(Integer userId){
        // Fetch the user and their subscription
        User user = userRepository.findById(userId).orElse(null);
        if (user == null || user.getSubscription() == null) {
            return 0;  // User or subscription not found
        }

        Subscription subscription = user.getSubscription();
        SubscriptionType userSubscriptionType = subscription.getSubscriptionType();
        int userAge = user.getAge();

        // Fetch all web series
        List<WebSeries> allSeries = webSeriesRepository.findAll();

        int count = 0;
        for (WebSeries series : allSeries) {
            // Check age eligibility and subscription level access
            if (series.getAgeLimit() <= userAge && isSubscriptionAllowed(userSubscriptionType, series.getSubscriptionType())) {
                count++;
            }
        }

        return count;
        //Return the count of all webSeries that a user can watch based on his ageLimit and subscriptionType
        //Hint: Take out all the Webseries from the WebRepository


      //  return null;
    }

    private boolean isSubscriptionAllowed(SubscriptionType userType, SubscriptionType seriesType) {
        // User with ELITE can watch everything
        if (userType == SubscriptionType.ELITE) return true;

        // PRO user can watch PRO and BASIC
        if (userType == SubscriptionType.PRO) {
            return seriesType == SubscriptionType.PRO || seriesType == SubscriptionType.BASIC;
        }

        // BASIC user can only watch BASIC
        return userType == SubscriptionType.BASIC && seriesType == SubscriptionType.BASIC;
    }


}
