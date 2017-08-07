package com.emc.s3;

import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.emc.vipr.services.s3.ViPRS3Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class BucketContentController {

    @Autowired
    private ViPRS3Client viPRS3Client;

    @RequestMapping("/bucket")
    public String listBucketContent(@RequestParam("bucketName") String bucketName, Model model) {

        try {
            viPRS3Client.getBucketAcl(bucketName);
            viPRS3Client.getNamespace();
            ObjectListing list = viPRS3Client.listObjects(bucketName);
            List<S3ObjectSummary> objectSummaries = list.getObjectSummaries();
            for(S3ObjectSummary objSummary : objectSummaries) {
                objSummary.getBucketName();
                objSummary.getKey();
                objSummary.getOwner();
                objSummary.getSize();
                objSummary.getLastModified();
                ObjectMetadata objectMetadata = viPRS3Client.getObjectMetadata(bucketName, objSummary.getKey());
                objectMetadata.getContentType();
                if(objectMetadata.getUserMetadata() != null) {
                    for(String key : objectMetadata.getUserMetadata().keySet()) {
                        objectMetadata.getUserMetadata().get(key);
                    }
                }
            }
            model.addAttribute("bucketsummaries", objectSummaries);
        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
            model.addAttribute("error", true);
            model.addAttribute("bucketsummaries", new ArrayList<S3ObjectSummary>());
        }

        return "listBucketContent";
    }

}
