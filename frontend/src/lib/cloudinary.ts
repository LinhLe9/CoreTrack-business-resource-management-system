import { CldUploadWidget } from 'next-cloudinary';

// Cloudinary configuration
export const cloudinaryConfig = {
  cloudName: 'dl6in7bwz',
  uploadPreset: 'my_unsigned_preset',  
};

// Helper function to get upload widget props
export const getUploadWidgetProps = (onUpload: (result: any) => void) => ({
  uploadPreset: cloudinaryConfig.uploadPreset,
  onUpload,
  options: {
    maxFiles: 1,
    sources: ['local', 'camera'],
    resourceType: 'image',
    clientAllowedFormats: ['jpg', 'jpeg', 'png', 'gif', 'webp'],
    maxFileSize: 10000000, // 10MB
  },
}); 