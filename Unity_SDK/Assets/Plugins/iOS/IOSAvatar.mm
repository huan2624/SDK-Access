#import "IOSAvatar.h"

@implementation IOSAvatar

static IOSAvatar* sharedObj = nil;

+ (IOSAvatar*) getInstance
{
    @synchronized (self)
    {
        if (sharedObj == nil)
            [[self alloc] init];
    }

    return sharedObj;
}

+ (id) allocWithZone:(NSZone *) zone
{
    @synchronized (self)
    {
        if (sharedObj == nil)
        {
            sharedObj = [super allocWithZone:zone];
            return sharedObj;
        }
    }

    return nil;
}

- (id) copyWithZone:(NSZone *) zone
{
    return self;
}

- (id) init
{
    @synchronized (self)
    {
        self = [super init];
        [UnityGetGLView() addSubview:self.view];
        return self;
    }
}

- (void) dealloc
{
    [self.view removeFromSuperview];
    //[super dealloc];
}

- (void) initUpAvatarConfig:(int) _quality Size:(int) _size PlayerId:(const char*) _playerId URL:(const char*) _url
{
    quality = _quality / 100.0f;
    scaleSize = _size;
    playerId = [[NSString alloc] initWithUTF8String:_playerId];
    url = [[NSString alloc] initWithUTF8String:_url];
}

- (void) openUpAvatarDialog:(const char*) _num Site:(const char*) _site
{
    num = [[NSString alloc] initWithUTF8String:_num];
    site = [[NSString alloc] initWithUTF8String:_site];
        
//    UIAlertController* alertMenu = [UIAlertController alertControllerWithTitle:@"请选择上传图片方式"
//                                                                   message:nil
//                                                            preferredStyle:UIAlertControllerStyleActionSheet];
//    [alertMenu addAction:[UIAlertAction actionWithTitle:@"本地相册"
//                                              style:UIAlertActionStyleDefault
//                                            handler:^(UIAlertAction *action) {
//                                                [self localPhoto];
//                                            }]];
//    [alertMenu addAction:[UIAlertAction actionWithTitle:@"相机拍照"
//                                              style:UIAlertActionStyleDefault
//                                            handler:^(UIAlertAction *action) {
//                                                [self takePhoto];
//                                            }]];
//    [alertMenu addAction:[UIAlertAction actionWithTitle:@"取消"
//                                              style:UIAlertActionStyleCancel
//                                            handler:^(UIAlertAction *action) {
//
//                                            }]];
//    
//    [self presentViewController:alertMenu animated:YES completion:nil];
    

    UIActionSheet* menu = [[UIActionSheet alloc]
                           initWithTitle:@"请选择上传图片方式"
                           delegate:self
                           cancelButtonTitle:@"取消"
                           destructiveButtonTitle:nil
                           otherButtonTitles:@"本地相册", @"相机拍照", nil];
    menu.actionSheetStyle = UIActionSheetStyleBlackTranslucent;
    [menu showInView:self.view];
    //[menu release];
}

- (void) actionSheet:(UIActionSheet*) actionSheet didDismissWithButtonIndex:(NSInteger) buttonIndex
{
    if(buttonIndex == 0)
    {
        [self localPhoto];
    }
    else if(buttonIndex == 1)
    {
        [self takePhoto];
    }
}

-(void) localPhoto
{
    UIImagePickerController* pickerImage = [[UIImagePickerController alloc] init];
    if([UIImagePickerController isSourceTypeAvailable:UIImagePickerControllerSourceTypePhotoLibrary]) {
        pickerImage.sourceType = UIImagePickerControllerSourceTypePhotoLibrary;
        pickerImage.mediaTypes = [UIImagePickerController availableMediaTypesForSourceType:pickerImage.sourceType];
    }
    pickerImage.delegate = self;
    pickerImage.allowsEditing = YES;
    [self presentModalViewController:pickerImage animated:YES];
    //[pickerImage release];
}

-(void) takePhoto
{
    UIImagePickerControllerSourceType sourceType = UIImagePickerControllerSourceTypeCamera;
    if ([UIImagePickerController isSourceTypeAvailable: UIImagePickerControllerSourceTypeCamera])
    {
        UIImagePickerController* picker = [[UIImagePickerController alloc] init];
        picker.delegate = self;
        picker.allowsEditing = YES;
        picker.sourceType = sourceType;
        [self presentModalViewController:picker animated:YES];
        //[picker release];
    }
}

-(void) imagePickerController:(UIImagePickerController*) picker didFinishPickingMediaWithInfo:(NSDictionary*) info
{
    if ([[info objectForKey:UIImagePickerControllerMediaType] isEqualToString:(__bridge NSString *)kUTTypeImage])
    {
        UIImage* image = [info objectForKey:UIImagePickerControllerEditedImage];
        UIImage* scaleImage = [self scaleToSize:image Size:CGSizeMake(scaleSize, scaleSize)];
        [picker dismissModalViewControllerAnimated:YES];
        [self showPreview:scaleImage];
    }
    else
    {
        [picker dismissModalViewControllerAnimated:YES];
        [self showErrorAlert];
    }
}

-(void) imagePickerControllerDidCancel:(UIImagePickerController*) picker
{
    [picker dismissModalViewControllerAnimated:YES];
    [self showCancelAlert];
}

- (void) showPreview:(UIImage*) image
{
    //MobileBridge::getInstance()->addMask();

    CGRect rect = [[UIScreen mainScreen] bounds];
    CGSize size = rect.size;

    mView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, size.width, size.height)];
    mView.backgroundColor = [UIColor whiteColor];
    [self.view addSubview:mView];

    UIToolbar* toolBar = [[UIToolbar alloc] init];
    [toolBar sizeToFit];

    toolBar.backgroundColor = [UIColor whiteColor];

    CGFloat toolbarHeight = [toolBar frame].size.height;

    CGFloat toolbarY = size.height - toolbarHeight;

    CGRect rectArea = CGRectMake(0, toolbarY, size.width, toolbarHeight);
    [toolBar setFrame:rectArea];

    UIBarButtonItem* titleButton = [[UIBarButtonItem alloc] initWithTitle:@" "
                                                                    style: UIBarButtonItemStylePlain
                                                                   target: self
                                                                   action: nil];

    UIBarButtonItem* rightButton = [[UIBarButtonItem alloc] initWithTitle:@" 上传 "
                                                                    style: UIBarButtonItemStyleDone
                                                                   target: self
                                                                   action: @selector(upClicked:)];
    UIBarButtonItem* leftButton  = [[UIBarButtonItem alloc] initWithTitle:@" 取消 "
                                                                    style: UIBarButtonItemStyleBordered
                                                                   target: self
                                                                   action: @selector(cancelClicked:)];
    UIBarButtonItem* fixedButton  = [[UIBarButtonItem alloc] initWithBarButtonSystemItem: UIBarButtonSystemItemFlexibleSpace
                                                                                  target: nil
                                                                                  action: nil];

    NSArray* array = [[NSArray alloc] initWithObjects:leftButton, fixedButton, titleButton, fixedButton, rightButton, nil];

    [toolBar setItems: array];

    //[titleButton release];
    //[leftButton  release];
    //[rightButton release];
    //[fixedButton release];
    //[array       release];

    [mView addSubview:toolBar];
    //[toolBar release];

    imageView = [[UIImageView alloc]initWithImage:image];
    imageView.frame = CGRectMake(0, 0, 320, 320);
    imageView.center = mView.center;
    [mView addSubview:imageView];
}

- (void) cancelClicked:(id) sender
{
    [self showCancelAlert];

    [imageView removeFromSuperview];
    [mView removeFromSuperview];
    //[imageView release];
    //[mView release];
    imageView = nil;
    mView = nil;

    //MobileBridge::getInstance()->removeMask();
}

- (void) upClicked:(id) sender
{
    [self showWaitingAlert];

    NSData* imageData = UIImageJPEGRepresentation(imageView.image, quality);
    NSString* imageString = [imageData base64Encoding];
    NSString* image64 = [self encodeURL:imageString];

    NSString* post = [NSString stringWithFormat:@"playerId=%@&img=%@&site=%@&photo=%@", playerId, num, site, image64];
    NSData* postData = [post dataUsingEncoding:NSUTF8StringEncoding allowLossyConversion:YES];
    NSString* postLength = [NSString stringWithFormat:@"%lu", (unsigned long)[postData length]];

    NSMutableURLRequest* request = [[NSMutableURLRequest alloc] init];
    [request setURL:[NSURL URLWithString:url]];
    [request setTimeoutInterval:30];
    [request setCachePolicy:NSURLRequestUseProtocolCachePolicy];
    [request setHTTPMethod:@"POST"];
    [request setValue:@"application/x-www-form-urlencoded" forHTTPHeaderField:@"Content-Type"];
    [request setValue:postLength forHTTPHeaderField:@"Content-Length"];
    [request setHTTPBody:postData];

    NSURLConnection* connection = [NSURLConnection alloc];
    [connection initWithRequest:request delegate:self];
}

-(void) connectionDidFinishLoading:(NSURLConnection *)connection
{
    [alert setTitle:@"上传成功"];
    [self performSelector:@selector(upSuccess) withObject:nil afterDelay:2.0f];
}

-(void) connection:(NSURLConnection *)connection didFailWithError:(NSError *)error
{
    [alert setTitle:@"上传失败，请重试"];
    [self performSelector:@selector(dismissAlert) withObject:nil afterDelay:2.0f];
}

- (void) upSuccess
{
    [self dismissAlert];
    [imageView removeFromSuperview];
    [mView removeFromSuperview];
    //[imageView release];
    //[mView release];
    imageView = nil;
    mView = nil;

    //MobileBridge::getInstance()->removeMask();
    //cocos2d::LuaEngine::getInstance()->getLuaStack()->executeFunctionByHandler(MobileBridge::getInstance()->getUpAvatarCallback(), 0);
    UnitySendMessage("MobileBridge", "ReceiveMessage", "上传成功");
}

- (void) showWaitingAlert
{
    alert = [[UIAlertView alloc] initWithTitle: @"上传中，请稍后..."
                                               message: nil
                                              delegate: nil
                                     cancelButtonTitle: nil
                                     otherButtonTitles: nil];
    [alert show];
}

- (void) showCancelAlert
{
    alert = [[UIAlertView alloc] initWithTitle: @"已取消"
                                                            message: nil
                                                           delegate: nil
                                                  cancelButtonTitle: nil
                                                  otherButtonTitles: nil];
    [alert show ];

    [self performSelector:@selector(dismissAlert) withObject:nil afterDelay:1.0f];
}

- (void) showErrorAlert
{
    alert = [[UIAlertView alloc] initWithTitle: @"只能选择图片"
                                       message: nil
                                      delegate: nil
                             cancelButtonTitle: nil
                             otherButtonTitles: nil];
    [alert show];

    [self performSelector:@selector(dismissAlert) withObject:nil afterDelay:2.0f];
}

- (void) dismissAlert
{
    if (alert != nil)
    {
        [alert dismissWithClickedButtonIndex:0 animated:YES];
        //[alert release];
        alert = nil;
    }
}

- (NSString*) encodeURL:(NSString*) string
{
    NSString* newString = (NSString*)CFBridgingRelease(CFURLCreateStringByAddingPercentEscapes( kCFAllocatorDefault, (CFStringRef)string, NULL, CFSTR(":/?#[]@!$ &'()*+,;=\"<>%{}|\\^~`"), kCFStringEncodingUTF8));
    if (newString)
        return newString;
    return @"";
}

- (UIImage*) scaleToSize:(UIImage *) image Size:(CGSize) size
{
    UIGraphicsBeginImageContext(size);
    [image drawInRect:CGRectMake(0,0, size.width, size.height)];
    UIImage* scaledImage = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    return scaledImage;
}

@end