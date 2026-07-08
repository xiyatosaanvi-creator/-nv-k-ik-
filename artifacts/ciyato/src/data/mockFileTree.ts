export type FileNode = {
  name: string;
  type: "folder" | "file";
  kind?: "pdf" | "image" | "video" | "doc" | "apk" | "audio" | "archive" | "other";
  size?: string;
  date?: string;
  children?: FileNode[];
};

// A simple simulated Android internal storage tree, used only to demonstrate
// real file-browser navigation (root -> folders -> contents). In the native
// app this is replaced by the real filesystem via Android's storage APIs.
export const deviceRoot: FileNode = {
  name: "Internal storage",
  type: "folder",
  children: [
    {
      name: "DCIM",
      type: "folder",
      children: [
        {
          name: "Camera",
          type: "folder",
          children: [
            { name: "IMG_20260601_091423.jpg", type: "file", kind: "image", size: "3.1 MB", date: "Jun 1" },
            { name: "IMG_20260602_142207.jpg", type: "file", kind: "image", size: "2.8 MB", date: "Jun 2" },
            { name: "VID_20260603_101500.mp4", type: "file", kind: "video", size: "24.6 MB", date: "Jun 3" },
          ],
        },
        { name: "Screenshots", type: "folder", children: [
          { name: "Screenshot_20260604.png", type: "file", kind: "image", size: "612 KB", date: "Jun 4" },
        ] },
      ],
    },
    {
      name: "Download",
      type: "folder",
      children: [
        { name: "Project_Proposal.pdf", type: "file", kind: "pdf", size: "2.4 MB", date: "Jun 5" },
        { name: "Brand_Deck.pptx", type: "file", kind: "doc", size: "18.7 MB", date: "Jun 6" },
        { name: "ciyato-release.apk", type: "file", kind: "apk", size: "41.2 MB", date: "Jun 6" },
      ],
    },
    {
      name: "Documents",
      type: "folder",
      children: [
        { name: "Taxes", type: "folder", children: [
          { name: "2025_return.pdf", type: "file", kind: "pdf", size: "1.1 MB", date: "Apr 2" },
        ] },
        { name: "Resume.docx", type: "file", kind: "doc", size: "88 KB", date: "May 12" },
      ],
    },
    {
      name: "Pictures",
      type: "folder",
      children: [
        { name: "Wallpapers", type: "folder", children: [
          { name: "midnight.jpg", type: "file", kind: "image", size: "4.4 MB", date: "Mar 3" },
        ] },
      ],
    },
    {
      name: "WhatsApp",
      type: "folder",
      children: [
        { name: "Media", type: "folder", children: [
          { name: "IMG-20260601-WA0007.jpg", type: "file", kind: "image", size: "220 KB", date: "Jun 1" },
        ] },
      ],
    },
    {
      name: "Music",
      type: "folder",
      children: [
        { name: "downloaded_track.mp3", type: "file", kind: "audio", size: "6.8 MB", date: "May 20" },
      ],
    },
  ],
};
