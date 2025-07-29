'use client';

import React, { useState } from 'react';
import {
  Box,
  Button,
  FormControl,
  FormLabel,
  Input,
  Textarea,
  VStack,
  Heading,
  useToast,
  FormErrorMessage,
  Select,
  HStack,
  Text,
} from '@chakra-ui/react';
import { useRouter } from 'next/navigation';
import currencyCodes from 'currency-codes';
import { addSupplier } from '@/services/supplierService';
import useCities from '@/hooks/useCities';
import useCountries from '@/hooks/useCountries';

interface AddSupplierForm {
  name: string;
  contactPerson: string;
  email: string;
  phone: string;
  address: string;
  city: string;
  country: string;
  website: string;
  currency: string;
  status: 'Active' | 'Inactive';
}

// initial the form
const AddSupplierPage: React.FC = () => {
  const [form, setForm] = useState<AddSupplierForm>({
    name: '',
    contactPerson: '',
    email: '',
    phone: '',
    address: '',
    city: '',
    country: '',
    website: '',
    currency: 'USD',
    status: 'Active',
  });

  const [errors, setErrors] = useState<Partial<AddSupplierForm>>({});
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [customCity, setCustomCity] = useState(''); // Separate state for custom city
  const toast = useToast();
  const router = useRouter();

  // handle currency
  const currencyList = currencyCodes.data.filter(c => c.code);
  const { countries: countryList, loading: countriesLoading } = useCountries();
  const { cities: cityList, loading: citiesLoading } = useCities(form.country);

  // Validation functions
  const validateEmail = (email: string): boolean => {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  };

  const validatePhone = (phone: string): boolean => {
    const phoneRegex = /^\+?[0-9 .-]{7,15}$/;
    return phoneRegex.test(phone);
  };

  const validateWebsite = (website: string): boolean => {
    if (!website) return true; // Optional field
    const websiteRegex = /^(https?:\/\/)?([\w.-]+)+(:[0-9]+)?(\/([\w/_.]*)?)?$/;
    return websiteRegex.test(website);
  };

  const validateForm = (): boolean => {
    const newErrors: Partial<AddSupplierForm> = {};

    // Name validation
    if (!form.name.trim()) {
      newErrors.name = 'Supplier name is required';
    } else if (form.name.length > 255) {
      newErrors.name = 'Supplier name cannot exceed 255 characters';
    }

    // Email validation
    if (form.email && !validateEmail(form.email)) {
      newErrors.email = 'Invalid email format';
    }

    // Phone validation
    if (form.phone && !validatePhone(form.phone)) {
      newErrors.phone = 'Invalid phone number format';
    }

    // Address validation
    if (form.address && form.address.length > 500) {
      newErrors.address = 'Address cannot exceed 500 characters';
    }

    // City validation
    if (form.city && form.city !== '__custom__' && form.city.length > 100) {
      newErrors.city = 'City cannot exceed 100 characters';
    }

    // Custom city validation
    if (form.city === '__custom__') {
      if (!customCity.trim()) {
        newErrors.city = 'Please enter a custom city name';
      } else if (customCity.length > 100) {
        newErrors.city = 'Custom city cannot exceed 100 characters';
      }
    }

    // Country validation
    if (form.country && form.country.length > 100) {
      newErrors.country = 'Country cannot exceed 100 characters';
    }

    // Website validation
    if (form.website && !validateWebsite(form.website)) {
      newErrors.website = 'Invalid website URL format';
    }

    // Currency validation
    if (form.currency && form.currency.length !== 3) {
      newErrors.currency = 'Currency must be 3 characters';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>
  ) => {
    const { name, value } = e.target;
    
    if (name === 'city') {
      if (value === '__custom__') {
        // User selected custom option
        setForm(prev => ({ ...prev, [name]: value }));
      } else if (value === '') {
        // User selected "Select from list" option
        setForm(prev => ({ ...prev, [name]: '' }));
        setCustomCity(''); // Clear custom city
      } else {
        // User selected a city from the list
        setForm(prev => ({ ...prev, [name]: value }));
        setCustomCity(''); // Clear custom city
      }
    } else {
      setForm(prev => ({ ...prev, [name]: value }));
    }
    
    // Clear error when user starts typing
    if (errors[name as keyof AddSupplierForm]) {
      setErrors(prev => ({ ...prev, [name]: undefined }));
    }

    // Reset city when country changes
    if (name === 'country') {
      setForm(prev => ({ ...prev, city: '' }));
      setCustomCity(''); // Clear custom city too
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!validateForm()) {
      toast({
        title: 'Validation Error',
        description: 'Please fix the errors in the form',
        status: 'error',
        duration: 3000,
        isClosable: true,
      });
      return;
    }

    setIsSubmitting(true);
    try {
      // Prepare form data with custom city if needed
      const submitData = {
        ...form,
        city: form.city === '__custom__' ? customCity : form.city
      };
      
      await addSupplier(submitData);
      toast({
        title: 'Supplier added successfully!',
        status: 'success',
        duration: 3000,
        isClosable: true,
      });
      router.push('/supplier');
    } catch (err: any) {
      toast({
        title: 'Failed to add supplier',
        description: err?.response?.data?.message || 'Check your data and try again.',
        status: 'error',
        duration: 4000,
        isClosable: true,
      });
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <Box maxW="800px" mx="auto" p={6}>
      <Heading mb={6}>Add New Supplier</Heading>
      <form onSubmit={handleSubmit}>
        <VStack spacing={4} align="stretch">
          {/* Supplier Name */}
          <FormControl isRequired isInvalid={!!errors.name}>
            <FormLabel>Supplier Name</FormLabel>
            <Input
              name="name"
              value={form.name}
              onChange={handleChange}
              placeholder="Enter supplier name"
              maxLength={255}
            />
            <FormErrorMessage>{errors.name}</FormErrorMessage>
          </FormControl>

          {/* Contact Person */}
          <FormControl>
            <FormLabel>Contact Person</FormLabel>
            <Input
              name="contactPerson"
              value={form.contactPerson}
              onChange={handleChange}
              placeholder="Enter contact person name"
            />
          </FormControl>

          {/* Email */}
          <FormControl isInvalid={!!errors.email}>
            <FormLabel>Email</FormLabel>
            <Input
              name="email"
              type="email"
              value={form.email}
              onChange={handleChange}
              placeholder="Enter email address"
            />
            <FormErrorMessage>{errors.email}</FormErrorMessage>
          </FormControl>

          {/* Phone */}
          <FormControl isInvalid={!!errors.phone}>
            <FormLabel>Phone</FormLabel>
            <Input
              name="phone"
              value={form.phone}
              onChange={handleChange}
              placeholder="Enter phone number (e.g., +1-555-123-4567)"
            />
            <FormErrorMessage>{errors.phone}</FormErrorMessage>
          </FormControl>

          {/* Address */}
          <FormControl isInvalid={!!errors.address}>
            <FormLabel>Address</FormLabel>
            <Textarea
              name="address"
              value={form.address}
              onChange={handleChange}
              placeholder="Enter full address"
              maxLength={500}
            />
            <FormErrorMessage>{errors.address}</FormErrorMessage>
          </FormControl>

          {/* Country and City */}
          <HStack spacing={4}>
            <FormControl isInvalid={!!errors.country}>
              <FormLabel>Country</FormLabel>
              <Select
                name="country"
                value={form.country}
                onChange={handleChange}
                placeholder={countriesLoading ? "Loading countries..." : "Select country"}
                isDisabled={countriesLoading}
              >
                {countryList.map(country => (
                  <option key={country} value={country}>
                    {country}
                  </option>
                ))}
              </Select>
              <FormErrorMessage>{errors.country}</FormErrorMessage>
            </FormControl>

            <FormControl isInvalid={!!errors.city}>
              <FormLabel>City</FormLabel>
              <Select
                name="city"
                value={form.city}
                onChange={handleChange}
                placeholder={citiesLoading ? "Loading cities..." : "Select city or type custom"}
                isDisabled={!form.country || citiesLoading}
              >
                <option value="">-- Select from list --</option>
                {cityList.map(city => (
                  <option key={city} value={city}>
                    {city}
                  </option>
                ))}
                <option value="__custom__">-- Or enter custom city --</option>
              </Select>
              <FormErrorMessage>{errors.city}</FormErrorMessage>
            </FormControl>

            {/* Custom City Input - Show when user selects custom option */}
            {form.city === '__custom__' && (
              <FormControl isInvalid={!!errors.city}>
                <FormLabel>Custom City</FormLabel>
                <Input
                  name="city"
                  value={customCity}
                  onChange={(e) => setCustomCity(e.target.value)}
                  placeholder="Enter your city name"
                  maxLength={100}
                />
                <FormErrorMessage>{errors.city}</FormErrorMessage>
              </FormControl>
            )}
          </HStack>

          {/* Website */}
          <FormControl isInvalid={!!errors.website}>
            <FormLabel>Website</FormLabel>
            <Input
              name="website"
              value={form.website}
              onChange={handleChange}
              placeholder="Enter website URL (e.g., https://example.com)"
            />
            <FormErrorMessage>{errors.website}</FormErrorMessage>
          </FormControl>

          {/* Currency */}
          <FormControl isRequired isInvalid={!!errors.currency}>
            <FormLabel>Currency</FormLabel>
            <Select
              name="currency"
              value={form.currency}
              onChange={handleChange}
              placeholder="Select currency"
            >
              {currencyList.map(curr => (
                <option key={curr.code} value={curr.code}>
                  {curr.code} - {curr.currency}
                </option>
              ))}
            </Select>
            <FormErrorMessage>{errors.currency}</FormErrorMessage>
          </FormControl>

          {/* Status */}
          <FormControl isRequired isInvalid={!!errors.status}>
            <FormLabel>Status</FormLabel>
            <Select
              name="status"
              value={form.status}
              onChange={handleChange}
              placeholder="Select status"
            >
              <option value="Active">Active</option>
              <option value="Inactive">Inactive</option>
            </Select>
            <FormErrorMessage>{errors.status}</FormErrorMessage>
          </FormControl>

          {/* Submit Button */}
          <Button
            type="submit"
            colorScheme="teal"
            size="lg"
            isLoading={isSubmitting}
            loadingText="Adding Supplier"
          >
            Add Supplier
          </Button>
        </VStack>
      </form>
    </Box>
  );
};

export default AddSupplierPage;
