package com.emc.s3;

import com.amazonaws.services.s3.model.*;
import com.emc.vipr.services.s3.ViPRS3Client;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
            List<BucketContentDto> bucketsummaries = new ArrayList<BucketContentDto>();
            for(S3ObjectSummary objSummary : objectSummaries) {
                BucketContentDto aDto = new BucketContentDto();
                aDto.setBucketName(objSummary.getBucketName());
                aDto.setKey(objSummary.getKey());
                aDto.setOwner("" + objSummary.getOwner().getDisplayName());
                aDto.setSize("" + objSummary.getSize());
                aDto.setLastModified("" + objSummary.getLastModified());
                ObjectMetadata objectMetadata = viPRS3Client.getObjectMetadata(bucketName, objSummary.getKey());
                objectMetadata.getContentType();
                if(objectMetadata.getUserMetadata() != null) {
                    StringBuilder meta = new StringBuilder();
                    for(String key : objectMetadata.getUserMetadata().keySet()) {
                        meta.append(key + "" + objectMetadata.getUserMetadata().get(key) + ";");
                    }
                }
                bucketsummaries.add(aDto);
            }
            model.addAttribute("bucketName", bucketName);
            model.addAttribute("bucketsummaries", bucketsummaries);
        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
            model.addAttribute("error", true);
            model.addAttribute("bucketName", bucketName);
            model.addAttribute("bucketsummaries", new ArrayList<BucketContentDto>());
        }
        return "listBucketContent";
    }

    @RequestMapping("/deletecontent")
    public String listBucketContent(@RequestParam("bucketName") String bucketName, @RequestParam("bucketKey") String bucketKey, Model model) {

        try {
            viPRS3Client.deleteObject(bucketName, bucketKey);
        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
            model.addAttribute("error", true);
        }
        return listBucketContent(bucketName, model);
    }

    @RequestMapping("/downloadcontent")
    public void downloadContent(@RequestParam("bucketName") String bucketName, @RequestParam("bucketKey") String bucketKey, HttpServletResponse response) {
            try {
                S3Object anObject = viPRS3Client.getObject(bucketName, bucketKey);

                // Set the content type and attachment header.
                response.addHeader("Content-disposition", "attachment;filename=" + anObject.getObjectMetadata().getUserMetadata().get("originalFilename"));
                response.setContentType("application/octet-stream");
                IOUtils.copy(anObject.getObjectContent(), response.getOutputStream());
                response.flushBuffer();
            } catch (Exception e) {

            }
        //return "redirect:/bucket?bucketName=" + bucketName;
    }


    @PostMapping("/bucketcontentupload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, @RequestParam String key, @RequestParam String bucketName, Model model) {
        System.out.println(file.getSize());
        System.out.println(file.getOriginalFilename());
        System.out.println(key);

        try {
            ObjectMetadata objectMetadata = new ObjectMetadata();
            Map<String, String> userMetadata = objectMetadata.getUserMetadata();
            userMetadata.put("originalFilename", file.getOriginalFilename());
            objectMetadata.setUserMetadata(userMetadata);
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, file.getInputStream(), objectMetadata);
            viPRS3Client.putObject(putObjectRequest);
        } catch (Exception e){
            model.addAttribute("message", e.getMessage());
            model.addAttribute("error", true);
        } finally {
            return listBucketContent(bucketName, model);
        }
    }

}
