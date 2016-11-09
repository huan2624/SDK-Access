//
//  IOSMethod.m
//  Unity-iPhone
//
//  Created by 大熊动漫 on 16/9/19.
//
//

#import "IOSAvatar.h"

extern "C"
{
    
    void iosInitUpAvatarConfig(int quality, int size, const char *playerId, const char *url)
    {
        NSLog(@"iosInitUpAvatarConfig---------");
        [[IOSAvatar getInstance] initUpAvatarConfig:quality Size:size PlayerId:playerId URL:url];
    }
    
    void iosOpenUpAvatarDialog(const char *num, const char *site)
    {
        NSLog(@"iosOpenUpAvatarDialog---------%@%@", [[NSString alloc] initWithUTF8String:num], [[NSString alloc] initWithUTF8String:site]);
        [[IOSAvatar getInstance] openUpAvatarDialog:num Site:site];
    }
    
}