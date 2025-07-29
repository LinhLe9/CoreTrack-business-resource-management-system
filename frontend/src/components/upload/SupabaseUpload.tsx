'use client';

import { useState, useId } from 'react';
import { Button, Box, Image, Text, VStack, HStack } from '@chakra-ui/react';
import { supabase } from '@/lib/supabase';

interface SupabaseUploadProps {
  onUpload: (url: string) => void;
  folder?: string;
  accept?: string;
  maxSize?: number; // in MB
  showPreview?: boolean; // New prop to control preview display
}

export default function SupabaseUpload({ 
  onUpload, 
  folder = 'uploads', 
  accept = 'image/*',
  maxSize = 5,
  showPreview = true
}: SupabaseUploadProps) {
  const [uploading, setUploading] = useState(false);
  const [preview, setPreview] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  
  // Generate unique IDs for this component instance
  const uniqueId = useId();
  const supabaseUploadId = `supabase-upload-${uniqueId}`;
  const localUploadId = `local-upload-${uniqueId}`;

  const handleFileSelect = async (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (!file) return;

    // Validate file size
    if (file.size > maxSize * 1024 * 1024) {
      setError(`File size must be less than ${maxSize}MB`);
      return;
    }

    // Validate file type
    if (!file.type.startsWith('image/')) {
      setError('Please select an image file');
      return;
    }

    setError(null);
    setUploading(true);

    try {
      // Create preview only if showPreview is true
      if (showPreview) {
        const reader = new FileReader();
        reader.onload = (e) => {
          setPreview(e.target?.result as string);
        };
        reader.readAsDataURL(file);
      }

      // Upload to Supabase
      const timestamp = Date.now();
      const fileName = `${folder}/${timestamp}_${file.name}`;
      
      console.log('Uploading to Supabase:', fileName);
      
      const { data, error: uploadError } = await supabase.storage
        .from('images')
        .upload(fileName, file, {
          cacheControl: '3600',
          upsert: false
        });

      if (uploadError) {
        console.error('Supabase upload error:', uploadError);
        throw uploadError;
      }

      console.log('Upload successful:', data);

      // Get public URL
      const { data: { publicUrl } } = supabase.storage
        .from('images')
        .getPublicUrl(fileName);

      console.log('Public URL:', publicUrl);
      onUpload(publicUrl);
    } catch (err) {
      console.error('Upload error:', err);
      setError('Failed to upload image. Please try again.');
    } finally {
      setUploading(false);
    }
  };

  const handleLocalFile = (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (!file) return;

    // Validate file size
    if (file.size > maxSize * 1024 * 1024) {
      setError(`File size must be less than ${maxSize}MB`);
      return;
    }

    // Validate file type
    if (!file.type.startsWith('image/')) {
      setError('Please select an image file');
      return;
    }

    setError(null);

    // Convert to base64
    const reader = new FileReader();
    reader.onload = (e) => {
      const base64 = e.target?.result as string;
      if (showPreview) {
        setPreview(base64);
      }
      onUpload(base64);
    };
    reader.readAsDataURL(file);
  };

  return (
    <VStack spacing={4} align="stretch">
      {error && (
        <Text color="red.500" fontSize="sm">
          {error}
        </Text>
      )}
      
      {showPreview && preview && (
        <Box>
          <Image 
            src={preview} 
            alt="Preview" 
            maxH="200px" 
            objectFit="contain"
            borderRadius="md"
          />
        </Box>
      )}

      <HStack spacing={3}>
        {/* Supabase Upload */}
        <Button
          as="label"
          htmlFor={supabaseUploadId}
          colorScheme="blue"
          isLoading={uploading}
          loadingText="Uploading..."
          cursor="pointer"
        >
          Upload to Supabase
        </Button>
        <input
          type="file"
          id={supabaseUploadId}
          accept={accept}
          onChange={handleFileSelect}
          style={{ display: 'none' }}
        />

        {/* Local File */}
        <Button
          as="label"
          htmlFor={localUploadId}
          colorScheme="green"
          cursor="pointer"
        >
          Choose Local File
        </Button>
        <input
          type="file"
          id={localUploadId}
          accept={accept}
          onChange={handleLocalFile}
          style={{ display: 'none' }}
        />
      </HStack>
    </VStack>
  );
} 