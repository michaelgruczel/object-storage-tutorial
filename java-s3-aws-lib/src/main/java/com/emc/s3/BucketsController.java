package com.emc.s3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
public class BucketsController {

    @Autowired
    private AmazonS3Client amazonS3Client;

    @RequestMapping("/")
    public String listBucketContent(Model model) {
        try {
            return getCurrentBuckets(model);
        } catch (Exception e) {
            return handleException(e, model);
        }
    }

    @RequestMapping("/deletebucket")
    public String deleteBucket(@RequestParam("bucketName") String bucketName, Model model) {
        try {
            amazonS3Client.deleteBucket(bucketName);
        } catch (Exception e) {
            handleException(e, model);
        } finally {
            return getCurrentBuckets(model);
        }
    }

    private String getCurrentBuckets(Model model) {
        List<Bucket> buckets = amazonS3Client.listBuckets();
        for (Bucket aBucket : buckets) {
            aBucket.getName();
        }
        model.addAttribute("buckets", buckets);
        model.addAttribute("bucket", new BucketDto());
        return "listBuckets";
    }

    public String handleException(Exception e, Model model) {

        List<Bucket> buckets = new ArrayList<>();
        model.addAttribute("buckets", buckets);
        model.addAttribute("bucket", new BucketDto());
        model.addAttribute("message", e.getMessage());
        model.addAttribute("error", true);
        return "listBuckets";
    }

    @RequestMapping(value = "/addBucket", method = RequestMethod.POST)
    public String addBucket(@ModelAttribute BucketDto bucketDto, Model model) {
        amazonS3Client.createBucket(bucketDto.getName());
        return getCurrentBuckets(model);
    }
}
