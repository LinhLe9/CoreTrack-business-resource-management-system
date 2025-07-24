"use client";

import {
  Box,
  Flex,
  Menu,
  MenuButton,
  MenuList,
  MenuItem,
  Button,
  IconButton,
  Drawer,
  DrawerOverlay,
  DrawerContent,
  DrawerCloseButton,
  DrawerHeader,
  DrawerBody,
  useDisclosure,
  Link as ChakraLink,
} from "@chakra-ui/react";
import { ChevronDownIcon, BellIcon, HamburgerIcon } from "@chakra-ui/icons";
import { FaUserCircle } from "react-icons/fa";
import NextLink from "next/link";
import Image from "next/image";
import { useRef } from "react";

export default function Header() {
  const { isOpen, onOpen, onClose } = useDisclosure();
  const btnRef = useRef<HTMLButtonElement>(null);

  return (
    <Box bg="white" borderBottom="1px solid" borderColor="gray.200" px={4} py={2}>
      <Flex maxW="7xl" mx="auto" justify="space-between" align="center">
        {/* Left side: Logo + Menus */}
        <Flex align="center" gap={4}>
          {/* Logo */}
          <NextLink href="/" passHref>
            <ChakraLink display="flex" alignItems="center">
              <Image src="/coretrack.jpg" alt="Logo" width={120} height={30} priority />
            </ChakraLink>
          </NextLink>

          {/* Desktop menu */}
          <Flex display={{ base: "none", md: "flex" }} gap={4}>
            {/* Resource */}
            <Menu>
              <MenuButton as={Button} variant="ghost" rightIcon={<ChevronDownIcon />}>
                Resource
              </MenuButton>
              <MenuList>
                <MenuItem as={NextLink} href="/product" passHref>
                  Product
                </MenuItem>
                <MenuItem as={NextLink} href="/material" passHref>
                  Material
                </MenuItem>
                <MenuItem as={NextLink} href="/supplier" passHref>
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
                <MenuItem as={NextLink} href="/product-inventory" passHref>
                  Product Inventory
                </MenuItem>
                <MenuItem as={NextLink} href="/material-inventory" passHref>
                  Material Inventory
                </MenuItem>
                <MenuItem as={NextLink} href="/alarm" passHref>
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
                <MenuItem as={NextLink} href="/production" passHref>
                  Production Ticket
                </MenuItem>
                <MenuItem as={NextLink} href="/purchasing" passHref>
                  Purchasing Ticket
                </MenuItem>
                <MenuItem as={NextLink} href="/sale" passHref>
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
          <IconButton aria-label="Notify" icon={<BellIcon />} variant="ghost" />

          <NextLink href="/profile" passHref>
            <ChakraLink display="flex" alignItems="center" gap={2} color="blue.600" _hover={{ color: "blue.700" }}>
              <FaUserCircle size={20} />
              <Box display={{ base: "none", sm: "block" }}>Student Portal</Box>
            </ChakraLink>
          </NextLink>
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
                <MenuItem as={NextLink} href="/product" passHref>
                  Product
                </MenuItem>
                <MenuItem as={NextLink} href="/material" passHref>
                  Material
                </MenuItem>
                <MenuItem as={NextLink} href="/supplier" passHref>
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
                <MenuItem as={NextLink} href="/product-inventory" passHref>
                  Product Inventory
                </MenuItem>
                <MenuItem as={NextLink} href="/material-inventory" passHref>
                  Material Inventory
                </MenuItem>
                <MenuItem as={NextLink} href="/alarm" passHref>
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
                <MenuItem as={NextLink} href="/production" passHref>
                  Production Ticket
                </MenuItem>
                <MenuItem as={NextLink} href="/purchasing" passHref>
                  Purchasing Ticket
                </MenuItem>
                <MenuItem as={NextLink} href="/sale" passHref>
                  Sale Invoice
                </MenuItem>
              </MenuList>
            </Menu>
          </DrawerBody>
        </DrawerContent>
      </Drawer>
    </Box>
  );
}