%% Event Classifier - Angathan FRANCIS
% Cumulative Volume of Tweets Initialization

function [e, ne, apple] = cumvol()

    % Event, 1 min bin, 3 hours window
    A(1, :) = [0, 1, 1, 1, 2, 3, 3, 3, 3, 15, 23, 43, 55, 71, 87, 95, 107, 115, 123, 135, 151, 155, 159, 159, 159, 159, 159, 159, 160, 160, 160, 160, 168, 171, 171, 171, 171, 171, 171, 171, 171, 171, 171, 171, 171, 171, 172, 172, 172, 172, 173, 173, 174, 174, 174, 174, 174, 174, 174, 174, 174, 174, 174, 174, 174, 174, 175, 175, 175, 175, 175, 175, 175, 175, 175, 175, 175, 175, 175, 175, 175, 175, 175, 175, 175, 175, 175, 175, 175, 175, 175, 175, 175, 175, 175, 175, 175, 175, 175, 175, 175, 175, 175, 175, 175, 175, 175, 175, 175, 175, 175, 175, 175, 175, 175, 175, 175, 175, 175, 175, 176, 176, 178, 182, 187, 188, 189, 190, 191, 191, 192, 192, 192, 192, 193, 194, 195, 195, 196, 196, 199, 199, 199, 200, 200, 200, 200, 200, 200, 200, 201, 201, 201, 201, 201, 201, 201, 201, 202, 202, 202, 202, 202, 202, 202, 202, 202, 202, 202, 202, 202, 202, 202, 202, 202, 202, 202, 202, 202];
    A(2, :) = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 8, 15, 23, 30, 41, 59, 63, 76, 83, 95, 102, 105, 115, 124, 128, 137, 150, 161, 168, 179, 187, 194, 204, 208, 214, 221, 225, 233, 235, 241, 246, 252, 254, 260, 267, 274, 281, 286, 293, 296, 301, 303, 307, 310, 321, 324, 330, 333, 333, 340, 341, 343, 344, 350, 350, 354, 355, 357, 361, 361, 364, 365, 365, 365, 365, 367, 374, 374, 377, 378, 379, 381, 381, 382, 383, 385, 385, 386, 386, 388, 388, 388, 390, 392, 392, 392, 392, 393, 393, 394, 394, 395, 395, 396, 398, 400, 400, 403, 403, 403, 405, 407, 408, 408, 409, 409, 409, 409, 411, 411, 411, 411, 411, 411, 411, 411, 412, 414, 416, 417, 417, 419, 422, 423, 423, 423, 423, 423, 423, 423, 424, 424, 424, 424, 424, 424, 424, 425, 426, 426, 431, 433, 433, 433, 433, 433, 435, 435, 435, 435, 435, 437, 437, 437, 437, 437, 437, 437, 437, 437, 437, 437, 439, 441, 441, 441, 441, 441];
    A(3, :) = [0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 2, 3, 4, 4, 4, 5, 5, 6, 6, 6, 6, 7, 7, 7, 7, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 10, 10, 10, 10, 10, 10, 11, 11, 11, 11, 11, 11, 11, 11, 11, 13, 13, 13, 13, 13, 13, 13, 13, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 17, 17, 18, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 22, 22, 22, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 25, 25, 25, 25, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 28, 28, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29];
    A(4, :) = [0, 1, 1, 2, 2, 6, 6, 12, 14, 14, 16, 16, 18, 23, 23, 28, 28, 30, 33, 37, 39, 45, 45, 45, 48, 51, 52, 58, 60, 65, 65, 66, 70, 70, 70, 70, 70, 70, 75, 78, 79, 80, 84, 84, 87, 87, 87, 89, 90, 92, 99, 105, 105, 107, 109, 111, 111, 116, 117, 119, 119, 123, 126, 130, 132, 133, 133, 135, 136, 141, 142, 145, 147, 151, 154, 155, 155, 156, 156, 159, 159, 159, 161, 162, 164, 165, 165, 167, 167, 171, 172, 172, 172, 174, 174, 174, 174, 174, 183, 193, 203, 217, 221, 222, 225, 226, 227, 236, 239, 240, 243, 246, 248, 251, 251, 252, 253, 256, 256, 261, 263, 265, 265, 267, 269, 271, 274, 280, 283, 285, 289, 294, 296, 301, 309, 313, 315, 319, 323, 326, 330, 335, 337, 339, 341, 341, 342, 344, 345, 346, 351, 353, 354, 357, 363, 365, 366, 370, 374, 375, 378, 379, 381, 381, 381, 388, 391, 393, 396, 400, 402, 405, 411, 416, 422, 424, 429, 433, 433];
    A(5, :) = [0, 3, 6, 9, 13, 15, 19, 23, 29, 31, 35, 39, 45, 50, 53, 54, 55, 60, 63, 64, 64, 70, 73, 79, 80, 83, 84, 85, 86, 86, 88, 90, 94, 94, 95, 99, 101, 101, 101, 104, 106, 106, 107, 109, 112, 112, 112, 113, 115, 118, 119, 120, 120, 121, 121, 124, 126, 127, 129, 129, 129, 130, 132, 133, 133, 135, 138, 140, 142, 143, 143, 143, 143, 144, 144, 144, 148, 152, 152, 152, 153, 153, 154, 154, 155, 157, 157, 157, 157, 157, 159, 159, 159, 159, 159, 159, 159, 160, 160, 161, 163, 165, 165, 167, 167, 169, 170, 170, 170, 172, 172, 174, 179, 179, 179, 181, 181, 186, 188, 188, 188, 188, 189, 190, 191, 192, 192, 192, 192, 192, 192, 192, 193, 193, 194, 195, 195, 195, 195, 195, 196, 198, 199, 199, 199, 199, 200, 200, 200, 200, 200, 200, 200, 201, 203, 204, 204, 204, 205, 208, 208, 209, 210, 212, 212, 212, 213, 213, 213, 214, 214, 216, 218, 220, 220, 220, 220, 220, 220];
    A(6, :) = [0, 1, 5, 9, 11, 11, 15, 17, 22, 24, 26, 30, 35, 38, 43, 44, 45, 46, 46, 47, 52, 55, 56, 60, 61, 62, 65, 67, 67, 71, 72, 74, 75, 75, 78, 81, 86, 88, 100, 130, 153, 168, 176, 188, 192, 199, 203, 210, 211, 215, 224, 235, 239, 242, 251, 252, 256, 259, 261, 261, 266, 267, 269, 270, 270, 270, 272, 276, 277, 278, 280, 280, 282, 283, 286, 287, 288, 289, 290, 292, 300, 301, 301, 302, 302, 302, 303, 305, 306, 306, 308, 308, 308, 309, 309, 309, 310, 312, 312, 314, 315, 319, 319, 320, 320, 320, 322, 323, 324, 324, 328, 329, 334, 335, 336, 337, 337, 340, 340, 340, 340, 340, 340, 340, 342, 342, 342, 342, 342, 344, 344, 344, 344, 344, 347, 348, 348, 348, 350, 350, 354, 354, 354, 354, 354, 354, 354, 354, 355, 355, 355, 355, 355, 355, 356, 356, 356, 356, 356, 356, 357, 357, 357, 357, 357, 357, 357, 357, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358];
    A(7, :) = [0, 1, 1, 1, 5, 5, 5, 5, 7, 10, 19, 31, 42, 52, 64, 71, 83, 91, 95, 106, 114, 123, 129, 140, 154, 168, 176, 181, 187, 193, 209, 214, 225, 235, 246, 255, 266, 271, 275, 288, 302, 314, 328, 331, 338, 351, 369, 394, 410, 425, 433, 444, 481, 525, 550, 629, 752, 862, 909, 958, 991, 1015, 1036, 1067, 1081, 1104, 1118, 1135, 1144, 1159, 1166, 1174, 1190, 1192, 1202, 1209, 1213, 1224, 1230, 1240, 1242, 1253, 1269, 1284, 1296, 1312, 1325, 1339, 1349, 1355, 1362, 1374, 1380, 1385, 1386, 1389, 1391, 1394, 1395, 1399, 1400, 1400, 1401, 1401, 1406, 1407, 1409, 1410, 1411, 1412, 1412, 1413, 1414, 1417, 1425, 1427, 1434, 1437, 1438, 1441, 1443, 1448, 1449, 1450, 1451, 1451, 1451, 1456, 1459, 1460, 1462, 1463, 1464, 1466, 1467, 1470, 1471, 1473, 1473, 1473, 1473, 1477, 1478, 1479, 1480, 1483, 1483, 1488, 1492, 1494, 1494, 1494, 1497, 1498, 1500, 1500, 1504, 1504, 1504, 1504, 1506, 1506, 1507, 1507, 1508, 1508, 1509, 1509, 1509, 1509, 1509, 1509, 1512, 1512, 1513, 1513, 1515, 1516, 1516];
    A(8, :) = [0, 15, 30, 38, 55, 60, 69, 74, 81, 84, 93, 96, 99, 103, 112, 116, 117, 117, 122, 130, 132, 136, 140, 142, 148, 149, 153, 157, 159, 161, 166, 169, 173, 178, 183, 192, 195, 196, 197, 201, 205, 210, 213, 219, 226, 235, 264, 282, 301, 318, 343, 364, 368, 379, 385, 388, 394, 409, 411, 416, 424, 429, 434, 446, 453, 459, 464, 469, 481, 497, 508, 521, 529, 541, 545, 559, 574, 587, 597, 603, 604, 612, 616, 618, 629, 639, 645, 686, 790, 863, 910, 958, 989, 1026, 1047, 1085, 1104, 1124, 1139, 1157, 1187, 1217, 1241, 1261, 1278, 1288, 1299, 1315, 1332, 1345, 1350, 1363, 1368, 1377, 1385, 1390, 1399, 1412, 1420, 1434, 1438, 1443, 1453, 1455, 1466, 1467, 1471, 1474, 1478, 1480, 1481, 1486, 1492, 1499, 1499, 1499, 1501, 1503, 1504, 1509, 1510, 1510, 1510, 1511, 1512, 1514, 1518, 1520, 1521, 1524, 1524, 1525, 1525, 1528, 1528, 1529, 1530, 1531, 1532, 1533, 1534, 1534, 1534, 1534, 1534, 1534, 1534, 1536, 1536, 1536, 1537, 1539, 1540, 1540, 1540, 1543, 1544, 1544, 1544];
    A(9, :) = [0, 1, 3, 3, 3, 4, 5, 5, 6, 6, 6, 7, 7, 7, 7, 7, 8, 8, 8, 9, 10, 11, 15, 17, 20, 20, 20, 22, 23, 27, 31, 35, 37, 39, 41, 44, 44, 46, 46, 47, 47, 47, 48, 50, 52, 56, 57, 57, 57, 58, 58, 58, 58, 59, 59, 62, 64, 65, 66, 66, 66, 66, 66, 66, 66, 66, 67, 68, 68, 68, 70, 70, 70, 70, 70, 71, 71, 72, 72, 74, 75, 75, 77, 77, 77, 78, 79, 79, 80, 80, 80, 80, 81, 81, 81, 81, 81, 81, 81, 83, 83, 85, 85, 85, 85, 85, 85, 85, 88, 90, 90, 90, 91, 91, 91, 93, 93, 93, 96, 97, 98, 98, 98, 98, 98, 98, 98, 98, 98, 99, 99, 99, 100, 100, 100, 100, 100, 101, 102, 104, 105, 105, 106, 106, 107, 108, 109, 109, 109, 109, 110, 110, 110, 110, 110, 110, 111, 112, 112, 112, 112, 114, 114, 114, 116, 117, 118, 119, 119, 119, 120, 122, 124, 124, 125, 125, 125, 125, 125];
    A(10, :) = [0, 0, 0, 1, 1, 1, 1, 1, 2, 4, 5, 5, 7, 8, 10, 11, 12, 12, 13, 15, 17, 18, 18, 18, 18, 18, 20, 20, 21, 22, 23, 23, 23, 23, 23, 23, 26, 27, 28, 29, 29, 29, 31, 32, 32, 35, 37, 38, 38, 38, 39, 39, 40, 42, 42, 42, 42, 43, 44, 44, 45, 46, 47, 47, 49, 49, 49, 50, 50, 50, 52, 52, 52, 52, 52, 53, 53, 54, 54, 55, 55, 56, 57, 57, 58, 60, 60, 60, 61, 62, 62, 63, 65, 66, 67, 67, 68, 69, 70, 71, 73, 73, 74, 75, 75, 75, 76, 77, 78, 78, 78, 78, 79, 79, 79, 81, 83, 84, 84, 84, 84, 84, 85, 85, 86, 86, 86, 86, 86, 87, 88, 88, 88, 88, 88, 88, 89, 89, 90, 91, 93, 94, 96, 96, 97, 97, 98, 98, 101, 101, 102, 105, 106, 106, 106, 108, 108, 110, 111, 112, 112, 114, 115, 116, 117, 118, 119, 120, 121, 123, 124, 126, 127, 129, 129, 129, 129, 129, 129];

    e = A;

    % Non-Event, 1 min bin, 3 hours window
    A1(1, :) = [0, 1, 1, 2, 4, 7, 7, 9, 11, 14, 14, 14, 15, 15, 18, 20, 20, 21, 23, 24, 26, 27, 27, 29, 29, 29, 30, 31, 31, 32, 33, 34, 36, 37, 37, 37, 39, 39, 41, 42, 42, 43, 45, 46, 46, 49, 50, 50, 51, 51, 51, 52, 52, 52, 52, 52, 52, 54, 56, 56, 56, 56, 56, 57, 58, 59, 60, 61, 61, 61, 61, 61, 63, 63, 63, 63, 64, 65, 67, 69, 69, 69, 69, 69, 69, 70, 70, 70, 72, 72, 73, 73, 75, 76, 76, 78, 79, 80, 81, 82, 82, 82, 82, 83, 83, 84, 84, 87, 88, 88, 88, 88, 88, 89, 89, 89, 90, 90, 92, 94, 94, 95, 97, 98, 99, 99, 99, 102, 102, 102, 105, 105, 106, 106, 108, 108, 109, 111, 111, 111, 114, 115, 115, 115, 115, 117, 117, 117, 120, 122, 122, 122, 123, 124, 124, 124, 124, 124, 124, 124, 126, 127, 127, 127, 127, 127, 128, 128, 130, 130, 130, 130, 131, 132, 133, 133, 133, 133, 133];
    A1(2, :) = [0, 1, 2, 2, 2, 4, 8, 9, 9, 10, 12, 14, 16, 18, 18, 19, 21, 21, 21, 24, 25, 28, 28, 31, 33, 33, 35, 36, 38, 41, 42, 44, 47, 47, 49, 50, 52, 53, 53, 54, 55, 58, 60, 60, 60, 60, 63, 65, 68, 70, 70, 72, 73, 74, 74, 76, 76, 79, 81, 81, 82, 84, 84, 86, 87, 90, 91, 92, 93, 93, 95, 97, 99, 101, 101, 101, 102, 105, 105, 106, 106, 106, 108, 110, 111, 111, 115, 117, 121, 122, 126, 128, 130, 130, 132, 134, 136, 137, 137, 139, 139, 139, 141, 141, 141, 142, 142, 146, 147, 151, 152, 153, 156, 156, 157, 158, 161, 162, 165, 166, 170, 171, 171, 175, 178, 179, 180, 182, 182, 183, 183, 185, 185, 189, 191, 192, 193, 194, 194, 195, 196, 197, 197, 198, 203, 204, 205, 205, 208, 208, 212, 214, 216, 217, 220, 222, 222, 224, 225, 225, 225, 228, 228, 231, 233, 234, 239, 239, 239, 241, 241, 242, 244, 248, 250, 250, 252, 255, 255];
    A1(3, :) = [0, 0, 2, 2, 4, 4, 6, 8, 10, 14, 14, 14, 16, 17, 17, 18, 21, 22, 26, 27, 27, 27, 28, 29, 34, 34, 37, 39, 43, 46, 46, 49, 53, 53, 55, 57, 57, 62, 63, 63, 63, 66, 67, 73, 74, 76, 77, 79, 82, 85, 87, 87, 91, 94, 96, 98, 99, 100, 101, 101, 103, 104, 107, 112, 113, 115, 116, 117, 118, 122, 126, 129, 131, 133, 133, 133, 133, 133, 137, 140, 140, 143, 145, 147, 150, 150, 155, 156, 157, 158, 161, 164, 165, 166, 168, 170, 172, 172, 174, 177, 178, 179, 181, 183, 185, 186, 188, 188, 188, 188, 191, 193, 195, 197, 199, 199, 199, 199, 201, 202, 204, 206, 207, 211, 211, 215, 217, 220, 220, 223, 224, 231, 234, 236, 242, 244, 245, 250, 250, 252, 253, 254, 255, 259, 264, 267, 269, 274, 276, 280, 280, 283, 285, 288, 290, 293, 295, 297, 300, 300, 303, 306, 308, 310, 312, 316, 321, 321, 321, 322, 326, 327, 329, 329, 334, 337, 342, 344, 344];
    A1(4, :) = [0, 1, 2, 6, 8, 10, 10, 14, 14, 14, 14, 18, 19, 21, 24, 27, 28, 28, 31, 32, 34, 34, 36, 38, 40, 41, 42, 43, 45, 48, 53, 54, 59, 61, 62, 62, 64, 70, 72, 73, 75, 79, 81, 83, 84, 86, 88, 90, 91, 91, 93, 96, 97, 98, 98, 100, 102, 102, 105, 107, 108, 108, 108, 110, 110, 116, 118, 121, 122, 124, 125, 126, 129, 131, 133, 142, 144, 145, 147, 149, 149, 151, 153, 157, 160, 160, 163, 165, 167, 169, 173, 174, 176, 176, 177, 179, 180, 180, 181, 182, 183, 184, 185, 186, 189, 189, 193, 193, 194, 195, 195, 197, 197, 199, 200, 204, 206, 207, 208, 210, 213, 214, 216, 217, 220, 222, 225, 225, 225, 226, 228, 230, 232, 233, 234, 235, 235, 235, 237, 239, 241, 242, 245, 246, 249, 253, 255, 256, 256, 256, 258, 261, 263, 263, 269, 272, 272, 278, 278, 281, 282, 283, 284, 287, 292, 295, 296, 297, 297, 299, 302, 305, 309, 311, 312, 314, 315, 315, 315];
    A1(5, :) = [0, 0, 3, 5, 6, 7, 7, 7, 7, 8, 9, 10, 12, 14, 16, 17, 17, 19, 20, 23, 27, 28, 29, 31, 31, 31, 33, 34, 38, 38, 38, 39, 40, 41, 41, 41, 42, 42, 42, 42, 42, 43, 48, 50, 50, 50, 50, 51, 54, 57, 60, 61, 62, 64, 65, 66, 69, 70, 71, 71, 72, 72, 73, 76, 79, 83, 87, 90, 90, 92, 92, 92, 93, 93, 96, 98, 102, 102, 104, 105, 106, 110, 110, 110, 110, 110, 111, 114, 118, 122, 122, 123, 124, 124, 124, 129, 129, 129, 130, 133, 135, 136, 136, 137, 139, 139, 149, 149, 151, 151, 157, 157, 159, 159, 159, 160, 161, 161, 161, 162, 163, 163, 166, 168, 169, 174, 174, 176, 177, 180, 180, 183, 183, 183, 183, 185, 188, 188, 195, 196, 196, 196, 197, 199, 199, 200, 204, 208, 208, 209, 211, 213, 213, 213, 213, 217, 218, 218, 218, 219, 220, 223, 224, 224, 226, 226, 228, 229, 229, 231, 232, 238, 238, 239, 239, 240, 242, 242, 242];
    A1(6, :) = [0, 5, 7, 9, 13, 15, 17, 23, 34, 40, 46, 49, 55, 67, 75, 82, 93, 95, 97, 108, 115, 117, 123, 128, 132, 142, 146, 153, 156, 167, 169, 174, 180, 183, 188, 190, 197, 203, 204, 207, 216, 220, 221, 227, 228, 230, 234, 238, 246, 249, 250, 254, 260, 270, 274, 278, 283, 285, 287, 291, 297, 305, 310, 312, 319, 324, 331, 337, 340, 345, 350, 359, 361, 365, 374, 378, 380, 385, 388, 390, 394, 401, 407, 413, 417, 419, 422, 433, 436, 437, 444, 448, 453, 458, 463, 466, 472, 476, 483, 488, 493, 496, 499, 504, 506, 513, 520, 529, 531, 536, 542, 547, 551, 553, 555, 557, 559, 564, 566, 570, 575, 583, 587, 593, 597, 600, 604, 606, 610, 612, 617, 625, 629, 634, 637, 641, 645, 646, 655, 657, 660, 663, 665, 667, 672, 675, 681, 683, 685, 685, 692, 696, 698, 705, 712, 722, 730, 736, 741, 744, 749, 754, 757, 759, 759, 762, 766, 770, 774, 789, 797, 802, 802, 807, 811, 818, 828, 840, 841];
    A1(7, :) = [0, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 4, 4, 5, 6, 6, 7, 7, 7, 9, 11, 12, 13, 13, 14, 14, 15, 17, 17, 17, 17, 18, 19, 19, 19, 19, 20, 20, 20, 21, 22, 23, 23, 24, 26, 28, 29, 30, 33, 35, 37, 38, 38, 39, 40, 40, 43, 44, 45, 45, 47, 47, 48, 48, 49, 51, 51, 52, 52, 52, 52, 53, 53, 53, 55, 55, 56, 56, 56, 56, 56, 56, 57, 57, 58, 58, 60, 60, 61, 61, 62, 63, 63, 65, 68, 69, 71, 74, 74, 75, 75, 76, 77, 77, 77, 78, 78, 79, 79, 80, 80, 82, 83, 84, 86, 89, 90, 90, 90, 92, 93, 96, 96, 97, 98, 98, 98, 99, 101, 103, 103, 103, 103, 105, 105, 106, 106, 107, 108, 108, 108, 109, 110, 111, 111, 111, 113, 113, 113, 113, 114, 114, 114, 114, 114, 114, 115, 115, 115, 116, 116, 117, 118, 118, 118, 119, 119, 120, 121, 121, 121, 121, 122, 123, 124, 124];
    A1(8, :) = [0, 5, 8, 12, 14, 15, 16, 22, 27, 34, 42, 43, 44, 48, 50, 53, 56, 57, 62, 66, 68, 74, 77, 81, 82, 89, 92, 93, 95, 102, 106, 108, 110, 111, 114, 119, 121, 122, 123, 126, 129, 131, 135, 136, 141, 145, 146, 150, 154, 157, 159, 162, 169, 176, 179, 183, 185, 190, 192, 192, 198, 201, 203, 204, 208, 213, 216, 218, 221, 224, 226, 228, 229, 229, 233, 235, 236, 241, 245, 253, 255, 256, 257, 257, 262, 262, 265, 270, 274, 275, 275, 276, 281, 281, 287, 289, 291, 293, 297, 299, 300, 303, 306, 306, 309, 312, 318, 321, 323, 324, 328, 332, 334, 335, 337, 341, 341, 343, 344, 346, 353, 355, 355, 358, 362, 363, 365, 367, 368, 372, 373, 376, 378, 378, 380, 383, 386, 389, 391, 394, 398, 399, 400, 402, 403, 405, 405, 407, 409, 409, 410, 413, 415, 415, 417, 417, 418, 419, 420, 420, 422, 422, 422, 423, 423, 423, 424, 424, 424, 426, 427, 428, 429, 430, 430, 431, 431, 433, 433];
    A1(9, :) = [0, 1, 3, 9, 9, 13, 20, 23, 25, 34, 41, 45, 47, 49, 51, 55, 58, 60, 63, 71, 73, 77, 82, 85, 88, 90, 92, 100, 102, 104, 105, 108, 112, 113, 116, 123, 126, 129, 135, 137, 139, 140, 144, 146, 153, 157, 165, 173, 179, 181, 185, 189, 191, 195, 197, 201, 204, 211, 213, 213, 219, 221, 224, 224, 227, 229, 230, 231, 233, 236, 241, 243, 246, 250, 251, 260, 261, 264, 267, 273, 274, 280, 285, 289, 290, 294, 298, 301, 303, 310, 318, 324, 326, 329, 334, 336, 337, 338, 338, 342, 343, 349, 351, 353, 357, 363, 366, 368, 372, 380, 383, 387, 391, 392, 396, 397, 400, 404, 409, 410, 411, 414, 417, 423, 423, 429, 435, 437, 440, 445, 447, 454, 457, 459, 461, 463, 465, 467, 473, 475, 480, 481, 488, 490, 493, 496, 499, 500, 502, 511, 515, 518, 523, 525, 526, 528, 528, 529, 531, 531, 533, 535, 535, 540, 541, 544, 549, 550, 551, 552, 553, 556, 558, 559, 560, 562, 568, 570, 570];
    A1(10, :) = [0, 0, 4, 8, 9, 9, 12, 17, 21, 21, 23, 24, 24, 27, 31, 32, 40, 46, 46, 46, 52, 53, 58, 60, 62, 67, 67, 73, 74, 78, 81, 83, 91, 92, 96, 102, 105, 111, 114, 115, 116, 116, 121, 121, 123, 131, 139, 140, 141, 142, 155, 158, 160, 162, 162, 168, 169, 175, 179, 182, 185, 187, 189, 189, 190, 194, 197, 197, 202, 203, 204, 205, 207, 212, 216, 220, 221, 227, 232, 238, 242, 247, 251, 252, 255, 257, 259, 261, 261, 266, 268, 270, 275, 276, 276, 277, 280, 281, 282, 288, 292, 293, 294, 295, 298, 299, 301, 307, 309, 310, 313, 318, 322, 325, 327, 332, 339, 342, 347, 350, 350, 356, 356, 360, 362, 363, 366, 370, 375, 378, 381, 384, 386, 390, 395, 398, 401, 403, 408, 409, 414, 419, 422, 429, 432, 434, 440, 443, 445, 449, 451, 460, 462, 465, 471, 472, 473, 476, 479, 484, 487, 492, 497, 505, 508, 511, 514, 517, 523, 524, 528, 531, 537, 541, 547, 556, 558, 564, 566];
    
    ne = A1;
    
    % Apple, 1 min bin, 1 hour window
    apple = [0, 34, 66, 91, 125, 153, 173, 201, 225, 254, 284, 315, 332, 354, 371, 398, 414, 435, 463, 487, 500, 524, 546, 561, 584, 599, 628, 650, 668, 678, 705, 722, 740, 757, 778, 806, 836, 852, 869, 901, 929, 952, 969, 994, 1014, 1033, 1052, 1068, 1088, 1104, 1110, 1123, 1133, 1147, 1161, 1179, 1193, 1200, 1210, 1228]
    % apple = [0, 0, 3, 3, 3, 3, 5, 6, 6, 6, 6, 6, 8, 8, 8, 10, 12, 14, 17, 17, 21, 21, 25, 30, 36, 40, 46, 48, 52, 56, 60, 68, 74, 76, 82, 90, 100, 104, 108, 108, 108, 110, 112, 116, 120, 122, 122, 128, 128, 132, 132, 136, 141, 143, 149, 151, 151, 151, 152, 152];
    
    % Non-Event
    % apple = [0, 4, 8, 18, 22, 26, 30, 35, 38, 40, 46, 47, 54, 57, 59, 66, 71, 75, 84, 86, 100, 102, 106, 115, 120, 124, 133, 137, 140, 144, 146, 153, 159, 164, 167, 174, 184, 189, 194, 198, 210, 212, 218, 224, 232, 237, 243, 249, 250, 258, 271, 274, 278, 283, 288, 292, 306, 310, 318, 320];
    apple = [0, 1, 2, 2, 2, 3, 4, 4, 4, 4, 5, 5, 5, 6, 6, 8, 10, 11, 12, 12, 12, 12, 13, 14, 14, 14, 15, 16, 18, 20, 20, 20, 20, 20, 20, 21, 23, 23, 25, 25, 25, 25, 26, 27, 27, 27, 27, 28, 28, 29, 29, 29, 30, 31, 33, 34, 34, 34, 35, 36];
end