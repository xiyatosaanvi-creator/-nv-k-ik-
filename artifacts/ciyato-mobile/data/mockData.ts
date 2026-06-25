export const mockApps = [
  { id: "1", name: "Instagram", category: "Social", color: "#E1306C", icon: "logo-instagram" },
  { id: "2", name: "WhatsApp", category: "Social", color: "#25D366", icon: "logo-whatsapp" },
  { id: "3", name: "Twitter", category: "Social", color: "#1DA1F2", icon: "logo-twitter" },
  { id: "4", name: "Telegram", category: "Social", color: "#0088CC", icon: "send" },
  { id: "5", name: "YouTube", category: "Media", color: "#FF0000", icon: "logo-youtube" },
  { id: "6", name: "Spotify", category: "Media", color: "#1DB954", icon: "musical-notes" },
  { id: "7", name: "Netflix", category: "Media", color: "#E50914", icon: "tv" },
  { id: "8", name: "Chrome", category: "Productivity", color: "#4285F4", icon: "globe" },
  { id: "9", name: "Gmail", category: "Productivity", color: "#EA4335", icon: "mail" },
  { id: "10", name: "Maps", category: "Productivity", color: "#4285F4", icon: "map" },
  { id: "11", name: "Calendar", category: "Productivity", color: "#1A73E8", icon: "calendar" },
  { id: "12", name: "Notes", category: "Productivity", color: "#FBBC04", icon: "document-text" },
  { id: "13", name: "Camera", category: "Tools", color: "#FF9500", icon: "camera" },
  { id: "14", name: "Files", category: "Tools", color: "#5E5CE6", icon: "folder" },
  { id: "15", name: "Settings", category: "Tools", color: "#6E6E73", icon: "settings" },
  { id: "16", name: "Calculator", category: "Tools", color: "#FF9500", icon: "calculator" },
  { id: "17", name: "Clock", category: "Tools", color: "#636366", icon: "time" },
  { id: "18", name: "Uber", category: "Transport", color: "#000000", icon: "car" },
  { id: "19", name: "Lyft", category: "Transport", color: "#FF00BF", icon: "car-sport" },
  { id: "20", name: "DoorDash", category: "Food", color: "#FF3008", icon: "restaurant" },
];

export const mockCategories = [
  { id: "1", name: "Social", count: 8, color: "#E1306C", icon: "people" },
  { id: "2", name: "Media", count: 12, color: "#1DB954", icon: "musical-notes" },
  { id: "3", name: "Productivity", count: 15, color: "#4285F4", icon: "briefcase" },
  { id: "4", name: "Tools", count: 9, color: "#FF9500", icon: "construct" },
  { id: "5", name: "Transport", count: 3, color: "#C6A15B", icon: "car" },
  { id: "6", name: "Food", count: 5, color: "#FF3008", icon: "restaurant" },
];

export const mockFiles = [
  { id: "1", name: "Project Proposal.pdf", size: "2.4 MB", type: "pdf", modified: "Today" },
  { id: "2", name: "Budget 2024.xlsx", size: "1.1 MB", type: "spreadsheet", modified: "Yesterday" },
  { id: "3", name: "Design Assets.zip", size: "45.2 MB", type: "archive", modified: "2 days ago" },
  { id: "4", name: "Meeting Notes.docx", size: "340 KB", type: "document", modified: "3 days ago" },
  { id: "5", name: "Presentation.pptx", size: "8.7 MB", type: "presentation", modified: "Last week" },
  { id: "6", name: "Invoice_2024.pdf", size: "560 KB", type: "pdf", modified: "Last week" },
];

export const mockFileCategories = [
  { id: "1", name: "Photos", size: "12.4 GB", count: 2847, color: "#7DB7FF", icon: "image" },
  { id: "2", name: "Videos", size: "8.2 GB", count: 142, color: "#E1306C", icon: "videocam" },
  { id: "3", name: "Documents", size: "1.8 GB", count: 567, color: "#C6A15B", icon: "document-text" },
  { id: "4", name: "Music", size: "4.1 GB", count: 892, color: "#1DB954", icon: "musical-notes" },
  { id: "5", name: "Downloads", size: "2.9 GB", count: 234, color: "#FF9500", icon: "download" },
  { id: "6", name: "Apps", size: "6.7 GB", count: 89, color: "#5E5CE6", icon: "apps" },
];

export const mockPhotoCollections = [
  { id: "1", name: "Beach Trip", count: 124, gradient: ["#0066CC", "#0099FF"], month: "June" },
  { id: "2", name: "Family Dinner", count: 48, gradient: ["#C6A15B", "#E2C37B"], month: "May" },
  { id: "3", name: "Hiking", count: 87, gradient: ["#39C66A", "#20B04B"], month: "April" },
  { id: "4", name: "City Lights", count: 63, gradient: ["#5E5CE6", "#4038CD"], month: "March" },
  { id: "5", name: "Sunsets", count: 41, gradient: ["#FF9500", "#FF6B00"], month: "March" },
  { id: "6", name: "Portraits", count: 156, gradient: ["#E1306C", "#C91C5A"], month: "Feb" },
];

export const mockSearchResults = [
  { id: "1", query: "photos from beach", type: "photos", count: 124 },
  { id: "2", query: "work documents 2024", type: "files", count: 23 },
  { id: "3", query: "screenshots", type: "photos", count: 312 },
  { id: "4", query: "large files", type: "files", count: 45 },
];

export const searchSuggestions = [
  "Show me photos from last month",
  "Find large files I can delete",
  "Screenshots from this week",
  "Documents I haven't opened",
  "Duplicate photos",
  "Videos longer than 5 minutes",
];

export const duplicateApps = [
  { name: "Camera", copies: 2, savings: "45 MB" },
  { name: "Browser", copies: 3, savings: "120 MB" },
  { name: "Music", copies: 2, savings: "38 MB" },
];
