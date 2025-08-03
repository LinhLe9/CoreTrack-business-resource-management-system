// Test script để tạo production ticket và kiểm tra status logs
const axios = require('axios');

const API_BASE_URL = 'http://localhost:8080/api';

// Test data
const testProductionTicket = {
  name: "Test Production Ticket",
  productVariants: [
    {
      productVariantSku: "PROD-001", // Thay bằng SKU thực tế trong database
      quantity: 10,
      expectedCompleteDate: "2025-08-15",
      boms: [
        {
          materialVariantSku: "MAT-001", // Thay bằng SKU thực tế
          plannedQuantity: 5,
          actualQuantity: 0
        }
      ]
    }
  ]
};

async function testCreateProductionTicket() {
  try {
    console.log('Creating production ticket...');
    
    const response = await axios.post(`${API_BASE_URL}/production-tickets/bulk-create`, testProductionTicket, {
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer YOUR_JWT_TOKEN' // Cần thay bằng token thực tế
      }
    });
    
    console.log('Response:', response.data);
    
    if (response.data.success) {
      console.log('Production ticket created successfully!');
      console.log('Created tickets:', response.data.createdTickets);
      
      // Kiểm tra status logs
      if (response.data.createdTickets.length > 0) {
        const ticketId = response.data.createdTickets[0].id;
        console.log(`Checking status logs for ticket ID: ${ticketId}`);
        
        const ticketResponse = await axios.get(`${API_BASE_URL}/production-tickets/${ticketId}`, {
          headers: {
            'Authorization': 'Bearer YOUR_JWT_TOKEN'
          }
        });
        
        console.log('Ticket details:', ticketResponse.data);
        console.log('Status logs count:', ticketResponse.data.logs?.length || 0);
      }
    }
  } catch (error) {
    console.error('Error:', error.response?.data || error.message);
  }
}

// Chạy test
testCreateProductionTicket(); 