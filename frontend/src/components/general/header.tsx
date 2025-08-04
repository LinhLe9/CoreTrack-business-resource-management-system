"use client";

import {
  Box,
  Flex,
  Menu,
  MenuButton,
  MenuList,
  MenuItem,
  MenuDivider,
  Button,
  IconButton,
  Drawer,
  DrawerOverlay,
  DrawerContent,
  DrawerCloseButton,
  DrawerHeader,
  DrawerBody,
  useDisclosure,
  HStack,
  Text,
} from "@chakra-ui/react";
import { ChevronDownIcon, HamburgerIcon } from "@chakra-ui/icons";
import { FaUserCircle } from "react-icons/fa";
import NextLink from "next/link";
import Image from "next/image";
import { useRef } from "react";
import NotificationBell from "@/components/NotificationBell";
import { useRouter } from "next/navigation";
import userService from "@/services/userService";

export default function Header() {
  const { isOpen, onOpen, onClose } = useDisclosure();
  const btnRef = useRef<HTMLButtonElement>(null);
  const router = useRouter();

  const handleLogout = async () => {
    try {
      await userService.logout();
      // Clear token from localStorage
      localStorage.removeItem('token');
      // Redirect to home page
      router.push('/');
    } catch (error) {
      console.error('Logout error:', error);
      // Fallback: clear token and redirect
      localStorage.removeItem('token');
      router.push('/');
    }
  };

  return (
    <Box bg="white" borderBottom="1px solid" borderColor="gray.200" px={4} py={2}>
      <Flex maxW="7xl" mx="auto" justify="space-between" align="center">
        {/* Left side: Logo + Menus */}
        <Flex align="center" gap={4}>
          {/* Logo */}
          <NextLink href="/dashboard" passHref>
            <Box display="flex" alignItems="center" cursor="pointer">
              <Image src="/coretrack.png" alt="Logo" width={120} height={30} priority />
            </Box>
          </NextLink>

          {/* Desktop menu */}
          <Flex display={{ base: "none", md: "flex" }} gap={4}>
            {/* Resource */}
            <Menu>
              <MenuButton as={Button} variant="ghost" rightIcon={<ChevronDownIcon />}>
                Resource
              </MenuButton>
              <MenuList>
                <MenuItem as="a" href="/product">
                  Product
                </MenuItem>
                <MenuItem as="a" href="/material">
                  Material
                </MenuItem>
                <MenuItem as="a" href="/supplier">
                  Supplier
                </MenuItem>
              </MenuList>
            </Menu>

            {/* Inventory */}
            <Menu>
              <MenuButton as={Button} variant="ghost" rightIcon={<ChevronDownIcon />}>
                Inventory
              </MenuButton>
              <MenuList>
                <MenuItem as="a" href="/product-inventory">
                  Product Inventory
                </MenuItem>
                <MenuItem as="a" href="/material-inventory">
                  Material Inventory
                </MenuItem>
                <MenuItem as="a" href="/alarm">
                  Alarm Management
                </MenuItem>
              </MenuList>
            </Menu>

            {/* Ticket */}
            <Menu>
              <MenuButton as={Button} variant="ghost" rightIcon={<ChevronDownIcon />}>
                Ticket
              </MenuButton>
              <MenuList>
                <MenuItem as="a" href="/production">
                  Production Ticket
                </MenuItem>
                <MenuItem as="a" href="/purchasing">
                  Purchasing Ticket
                </MenuItem>
                <MenuItem as="a" href="/sale">
                  Sale Invoice
                </MenuItem>
              </MenuList>
            </Menu>
        </Flex>

          {/* Mobile hamburger */}
          <IconButton
            ref={btnRef}
            aria-label="Menu"
            icon={<HamburgerIcon />}
            variant="ghost"
            display={{ base: "flex", md: "none" }}
            onClick={onOpen}
          />
        </Flex>

        {/* Right side: User actions */}
        <Flex align="center" gap={4}>
          <NotificationBell />

          {/* User Menu */}
          <Menu>
            <MenuButton as={Button} variant="ghost" rightIcon={<ChevronDownIcon />}>
              <HStack spacing={2}>
                <FaUserCircle size={16} />
                <Text display={{ base: "none", sm: "block" }}>User</Text>
              </HStack>
            </MenuButton>
            <MenuList>
              <MenuItem as="a" href="/user-management">
                Admin Dashboard
              </MenuItem>
              <MenuItem as="a" href="/profile">
                Profile
              </MenuItem>
              <MenuDivider />
              <MenuItem onClick={handleLogout} color="red.500">
                Logout
              </MenuItem>
            </MenuList>
          </Menu>
        </Flex>
      </Flex>

      {/* Drawer menu for mobile */}
      <Drawer isOpen={isOpen} placement="left" onClose={onClose} finalFocusRef={btnRef}>
        <DrawerOverlay />
        <DrawerContent>
          <DrawerCloseButton />
          <DrawerHeader>Menu</DrawerHeader>

          <DrawerBody display="flex" flexDirection="column" gap={4}>
            <Menu>
              <MenuButton as={Button} variant="ghost" rightIcon={<ChevronDownIcon />}>
                Resource
              </MenuButton>
              <MenuList>
                <MenuItem as="a" href="/product">
                  Product
                </MenuItem>
                <MenuItem as="a" href="/material">
                  Material
                </MenuItem>
                <MenuItem as="a" href="/supplier">
                  Supplier
                </MenuItem>
              </MenuList>
            </Menu>

            {/* Inventory */}
            <Menu>
              <MenuButton as={Button} variant="ghost" rightIcon={<ChevronDownIcon />}>
                Inventory
              </MenuButton>
              <MenuList>
                <MenuItem as="a" href="/product-inventory">
                  Product Inventory
                </MenuItem>
                <MenuItem as="a" href="/material-inventory">
                  Material Inventory
                </MenuItem>
                <MenuItem as="a" href="/alarm">
                  Alarm Management
                </MenuItem>
              </MenuList>
            </Menu>

            {/* Ticket */}
            <Menu>
              <MenuButton as={Button} variant="ghost" rightIcon={<ChevronDownIcon />}>
                Ticket
              </MenuButton>
              <MenuList>
                <MenuItem as="a" href="/production">
                  Production Ticket
                </MenuItem>
                <MenuItem as="a" href="/purchasing">
                  Purchasing Ticket
                </MenuItem>
                <MenuItem as="a" href="/sale">
                  Sale Invoice
                </MenuItem>
              </MenuList>
            </Menu>

            {/* User Menu for Mobile */}
            <Menu>
              <MenuButton as={Button} variant="ghost" rightIcon={<ChevronDownIcon />}>
                <HStack spacing={2}>
                  <FaUserCircle size={16} />
                  <Text>User</Text>
                </HStack>
              </MenuButton>
              <MenuList>
                <MenuItem as="a" href="/admin">
                  Admin Dashboard
                </MenuItem>
                <MenuItem as="a" href="/profile">
                  Profile
                </MenuItem>
                <MenuDivider />
                <MenuItem onClick={handleLogout} color="red.500">
                  Logout
                </MenuItem>
              </MenuList>
            </Menu>
          </DrawerBody>
        </DrawerContent>
      </Drawer>
    </Box>
  );
}