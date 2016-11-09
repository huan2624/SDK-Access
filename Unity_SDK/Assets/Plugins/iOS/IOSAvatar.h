#ifndef __IOS_AVATAR_H
#define __IOS_AVATAR_H

#import <UIKit/UIKit.h>
#import <MobileCoreServices/MobileCoreServices.h>

@interface IOSAvatar : UIViewController<UIActionSheetDelegate, UIImagePickerControllerDelegate, UINavigationControllerDelegate>
{
    UIView* mView;
    
    UIImageView* imageView;
    
    UIAlertView* alert;
    
    CGFloat quality;
    
    CGFloat scaleSize;
    
    NSString* playerId;
    
    NSString* url;
    
    NSString* num;
    
    NSString* site;
}

+ (IOSAvatar*) getInstance;

- (void) initUpAvatarConfig:(int)_quality Size:(int) _size PlayerId:(const char*) _playerId URL:(const char*) _url;

- (void) openUpAvatarDialog:(const char*) _num Site:(const char*) _site;

- (void) localPhoto;

- (void) takePhoto;

- (void) showPreview:(UIImage*) image;

- (void) showWaitingAlert;

- (void) showCancelAlert;

- (void) dismissAlert;

- (NSString*) encodeURL:(NSString*) string;

- (UIImage*) scaleToSize:(UIImage *) image Size:(CGSize) size;

@end

#endif