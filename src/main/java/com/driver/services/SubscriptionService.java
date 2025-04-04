package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;


    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto) {
        User user = userRepository.findById(subscriptionEntryDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + subscriptionEntryDto.getUserId()));

        Subscription subscription = new Subscription();
        subscription.setSubscriptionType(subscriptionEntryDto.getSubscriptionType());
        subscription.setNoOfScreensSubscribed(subscriptionEntryDto.getNoOfScreensRequired());
        subscription.setStartSubscriptionDate(new Date());

        int totalAmount = 0;
        SubscriptionType type = subscriptionEntryDto.getSubscriptionType();
        int screens = subscriptionEntryDto.getNoOfScreensRequired();

        if (type == SubscriptionType.BASIC) {
            totalAmount = 500 + (200 * screens);
        } else if (type == SubscriptionType.PRO) {
            totalAmount = 800 + (250 * screens);
        } else if (type == SubscriptionType.ELITE) {
            totalAmount = 1000 + (350 * screens);
        }

        subscription.setTotalAmountPaid(totalAmount);
        subscription.setUser(user);
        user.setSubscription(subscription);

        subscriptionRepository.save(subscription);
        userRepository.save(user);

        return totalAmount;
    }


    public Integer upgradeSubscription(Integer userId)throws Exception{


        User user = userRepository.findById(userId).get();
        Subscription subscription = user.getSubscription();
        SubscriptionType currentType = subscription.getSubscriptionType();
        int screens = subscription.getNoOfScreensSubscribed();
        int currentAmount = subscription.getTotalAmountPaid();

        int newAmount = 0;
        SubscriptionType newType = null;

        if (currentType == SubscriptionType.BASIC) {
            newType = SubscriptionType.PRO;
            newAmount = 800 + (250 * screens);
        } else if (currentType == SubscriptionType.PRO) {
            newType = SubscriptionType.ELITE;
            newAmount = 1000 + (350 * screens);
        } else if (currentType == SubscriptionType.ELITE) {
            throw new Exception("Already the best Subscription");
        }

        int difference = newAmount - currentAmount;
        subscription.setSubscriptionType(newType);
        subscription.setTotalAmountPaid(newAmount);
        subscriptionRepository.save(subscription);

        return difference;
        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository

        //return null;
    }

    public Integer calculateTotalRevenueOfHotstar(){



        List<Subscription> subscriptions = subscriptionRepository.findAll();
        int totalRevenue = 0;

        for (Subscription subscription : subscriptions) {
            totalRevenue += subscription.getTotalAmountPaid();
        }

        return totalRevenue;
        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb

       // return null;
    }

}
