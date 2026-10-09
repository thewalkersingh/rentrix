IAM user has the right policy:
{
  "Version": "2012-10-17",
  "Statement": [{
    "Effect": "Allow",
    "Action": [
      "s3:PutObject",
      "s3:GetObject",
      "s3:DeleteObject"
    ],
    "Resource": "arn:aws:s3:::rentrix-media/*"
  }]
}
-----------------------Bucket public access--------------------------------------
Option A: Make the bucket public (simpler, recommended for flat photos)
In S3 Console:
Permissions tab → Block public access → Edit → Uncheck all → Save
Bucket policy → Edit → paste:
{
  "Version": "2012-10-17",
  "Statement": [{
    "Sid": "PublicReadGetObject",
    "Effect": "Allow",
    "Principal": "*",
    "Action": "s3:GetObject",
    "Resource": "arn:aws:s3:::rentrix-media/*"
  }]
}

------------------------------------------------
A Quick Troubleshooting Note

The policy you provided grants access to the objects inside the bucket (rentrix-media/*), which is perfect for PutObject, GetObject, and DeleteObject.
However, if your Spring Boot application uses a library (like Spring Cloud AWS) that automatically attempts to check if the bucket exists when starting up, it will call s3:ListBucket. Your application will throw an Access Denied error unless you also give permission to the bucket root.
If you encounter this startup error, update your policy to look like this instead:

{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "s3:PutObject",
        "s3:GetObject",
        "s3:DeleteObject"
      ],
      "Resource": "arn:aws:s3:::rentrix-media/*"
    },
    {
      "Effect": "Allow",
      "Action": "s3:ListBucket",
      "Resource": "arn:aws:s3:::rentrix-media"
    }
  ]
}
------------------------------------------------------------------------
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/media")
public class MediaUploadController {

    private final String BUCKET_NAME = "rentrix-media";
    // Adjust if your bucket is in a specific region, e.g., s3.us-west-2.amazonaws.com
    private final String S3_BASE_URL = "https://" + BUCKET_NAME + ".s3.amazonaws.com/"; 

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        String fileName = file.getOriginalFilename();
        
        // 1. Your S3 upload logic goes here...
        // s3Client.putObject( ... );

        // 2. Construct and return the public URL string
        String publicUrl = S3_BASE_URL + fileName;
        return publicUrl; // Returns "https://amazonaws.com"
    }
}
--------------------------------------------------------